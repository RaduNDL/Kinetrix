using System.Text.RegularExpressions;
using FitnessApp.Application.DTOs;
using FitnessApp.Application.Exceptions;
using FitnessApp.Application.Interfaces;
using FitnessApp.Infrastructure.Data;
using FitnessApp.Infrastructure.Services;
using Microsoft.Data.Sqlite;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Logging.Abstractions;
using Xunit;

namespace FitnessApp.Tests;

public sealed class AuthFlowTests : IDisposable
{
    private readonly SqliteConnection _connection = new("Data Source=:memory:");
    private readonly ApplicationDbContext _database;
    private readonly CapturingEmailService _email = new();
    private readonly AuthService _auth;

    public AuthFlowTests()
    {
        _connection.Open();
        _database = new ApplicationDbContext(new DbContextOptionsBuilder<ApplicationDbContext>()
            .UseSqlite(_connection).Options);
        _database.Database.EnsureCreated();
        var config = new ConfigurationBuilder().AddInMemoryCollection(new Dictionary<string, string?>
        {
            ["JwtSettings:SecretKey"] = "AutomatedTestsOnly_012345678901234567890123456789"
        }).Build();
        _auth = new AuthService(_database, config, _email, NullLogger<AuthService>.Instance);
    }

    private static RegisterRequestDto Registration(string email = "person@example.org") => new()
    {
        Email = email, Password = "TestPassword123!", FirstName = "Alex", LastName = "Test",
        DateOfBirth = new DateTime(2000, 1, 1), Gender = "Prefer not to say", HeightCm = 175
    };

    private string LastCode => Regex.Match(_email.Messages.Last().Body, @">(\d{6})</p>").Groups[1].Value;
    private Task<AuthResponseDto> Verify(string code) => _auth.VerifyEmailAsync(new()
        { Email = "person@example.org", Code = code });

    [Fact]
    public async Task RegistrationRequiresVerificationAndAllowsNonGmailAddresses()
    {
        var response = await _auth.RegisterAsync(Registration("  PERSON@EXAMPLE.ORG  "));
        Assert.Equal("person@example.org", response.Email);
        var user = await _database.Users.SingleAsync();
        Assert.False(user.IsEmailVerified);
        Assert.NotEqual("TestPassword123!", user.PasswordHash);
        Assert.Single(_email.Messages);
        Assert.Equal(6, LastCode.Length);
        Assert.NotEqual(LastCode, user.EmailVerificationCodeHash);
        await Assert.ThrowsAsync<EmailNotVerifiedException>(() => _auth.LoginAsync(new()
            { Email = response.Email, Password = "TestPassword123!" }));
    }

    [Fact]
    public async Task CorrectCodeActivatesAccountSendsWelcomeAndCanOnlyBeUsedOnce()
    {
        await _auth.RegisterAsync(Registration());
        var code = LastCode;
        var response = await Verify(code);
        Assert.False(string.IsNullOrWhiteSpace(response.Token));
        Assert.True((await _database.Users.SingleAsync()).IsEmailVerified);
        Assert.Null((await _database.Users.SingleAsync()).EmailVerificationCodeHash);
        Assert.Equal(2, _email.Messages.Count);
        Assert.Contains("Welcome", _email.Messages[1].Body);
        var login = await _auth.LoginAsync(new() { Email = response.Email, Password = "TestPassword123!" });
        Assert.Equal(response.UserId, login.UserId);
        await Assert.ThrowsAsync<InvalidOperationException>(() => Verify(code));
        Assert.Equal(2, _email.Messages.Count);
    }

    [Fact]
    public async Task WrongCodesLockVerificationUntilResend()
    {
        await _auth.RegisterAsync(Registration());
        var correctCode = LastCode;
        var wrongCode = correctCode == "000000" ? "111111" : "000000";
        for (var attempt = 0; attempt < 5; attempt++)
            await Assert.ThrowsAsync<InvalidOperationException>(() => Verify(wrongCode));
        await Assert.ThrowsAsync<InvalidOperationException>(() => Verify(correctCode));
        Assert.False((await _database.Users.SingleAsync()).IsEmailVerified);
    }

    [Fact]
    public async Task ExpiredCodeCannotActivateAccount()
    {
        await _auth.RegisterAsync(Registration());
        var code = LastCode;
        var user = await _database.Users.SingleAsync();
        user.EmailVerificationExpiresAtUtc = DateTime.UtcNow.AddSeconds(-1);
        await _database.SaveChangesAsync();
        await Assert.ThrowsAsync<InvalidOperationException>(() => Verify(code));
        Assert.False(user.IsEmailVerified);
        Assert.Null(user.EmailVerificationCodeHash);
    }

    [Fact]
    public async Task ResendHasCooldownAndReplacesPreviousCodeHash()
    {
        await _auth.RegisterAsync(Registration());
        var user = await _database.Users.SingleAsync();
        await Assert.ThrowsAsync<InvalidOperationException>(() => _auth.ResendVerificationAsync(new()
            { Email = user.Email }));
        user.EmailVerificationLastSentAtUtc = DateTime.UtcNow.AddMinutes(-2);
        user.EmailVerificationAttempts = 5;
        await _database.SaveChangesAsync();
        await _auth.ResendVerificationAsync(new() { Email = user.Email });
        Assert.Equal(0, user.EmailVerificationAttempts);
        Assert.Equal(2, _email.Messages.Count);
        await Verify(LastCode);
        Assert.True(user.IsEmailVerified);
    }

    [Fact]
    public async Task EmailFailureLeavesRecoverableUnverifiedAccountAndDoesNotClaimSuccess()
    {
        _email.Fail = true;
        await Assert.ThrowsAsync<EmailDeliveryException>(() => _auth.RegisterAsync(Registration()));
        var user = await _database.Users.SingleAsync();
        Assert.False(user.IsEmailVerified);
        Assert.Null(user.EmailVerificationCodeHash);
        Assert.Null(user.EmailVerificationLastSentAtUtc);
        await Assert.ThrowsAsync<EmailNotVerifiedException>(() => _auth.RegisterAsync(Registration()));
        _email.Fail = false;
        await _auth.ResendVerificationAsync(new() { Email = user.Email });
        await Verify(LastCode);
        Assert.True(user.IsEmailVerified);
    }

    [Fact]
    public async Task WelcomeFailureDoesNotUndoVerificationOrBlockLogin()
    {
        await _auth.RegisterAsync(Registration());
        var code = LastCode;
        _email.Fail = true;
        var response = await Verify(code);
        Assert.False(string.IsNullOrEmpty(response.Token));
        await _auth.LoginAsync(new() { Email = response.Email, Password = "TestPassword123!" });
    }

    [Fact]
    public async Task DuplicateVerifiedAccountCannotOverwritePassword()
    {
        await _auth.RegisterAsync(Registration());
        await Verify(LastCode);
        var originalHash = (await _database.Users.SingleAsync()).PasswordHash;
        var duplicate = Registration();
        duplicate.Password = "SomeOtherPassword123";
        await Assert.ThrowsAsync<InvalidOperationException>(() => _auth.RegisterAsync(duplicate));
        Assert.Equal(originalHash, (await _database.Users.SingleAsync()).PasswordHash);
    }

    [Fact]
    public async Task InvalidCredentialsDoNotRevealPendingAccountStatus()
    {
        await _auth.RegisterAsync(Registration());
        await Assert.ThrowsAsync<UnauthorizedAccessException>(() => _auth.LoginAsync(new()
            { Email = "person@example.org", Password = "WrongPassword" }));
    }

    [Fact]
    public async Task UnknownEmailResendDoesNotCreateAccountOrSendMail()
    {
        await _auth.ResendVerificationAsync(new() { Email = "unknown@example.org" });
        Assert.Empty(_email.Messages);
        Assert.Empty(await _database.Users.ToListAsync());
    }

    [Fact]
    public async Task UnicodePasswordOverBcryptByteLimitIsRejected()
    {
        var request = Registration();
        request.Password = new string('ă', 40);
        await Assert.ThrowsAsync<InvalidOperationException>(() => _auth.RegisterAsync(request));
        Assert.Empty(await _database.Users.ToListAsync());
    }

    [Fact]
    public async Task CodeForAnotherAccountCannotVerifyThisAccount()
    {
        await _auth.RegisterAsync(Registration());
        var correctCode = LastCode;
        await _auth.RegisterAsync(Registration("other@example.org"));
        var otherCode = LastCode;
        // A random six-digit collision is valid for both accounts, so choose a
        // definitely incorrect code rather than making the test probabilistic.
        if (correctCode == otherCode) otherCode = correctCode == "000000" ? "111111" : "000000";
        await Assert.ThrowsAsync<InvalidOperationException>(() => Verify(otherCode));
        Assert.False((await _database.Users.SingleAsync(u => u.Email == "person@example.org")).IsEmailVerified);
    }

    public void Dispose() { _database.Dispose(); _connection.Dispose(); }

    private sealed class CapturingEmailService : IEmailService
    {
        public bool Fail { get; set; }
        public List<(string To, string Subject, string Body)> Messages { get; } = [];
        public Task SendEmailAsync(string toEmail, string subject, string bodyHtml)
        {
            if (Fail) throw new IOException("Simulated email outage");
            Messages.Add((toEmail, subject, bodyHtml));
            return Task.CompletedTask;
        }
    }
}
