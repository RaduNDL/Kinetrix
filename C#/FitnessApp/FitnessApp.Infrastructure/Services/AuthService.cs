using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Security.Cryptography;
using System.Text;
using System.Text.Encodings.Web;
using FitnessApp.Application.DTOs;
using FitnessApp.Application.Exceptions;
using FitnessApp.Application.Interfaces;
using FitnessApp.Domain.Entities;
using FitnessApp.Infrastructure.Data;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Logging;
using Microsoft.IdentityModel.Tokens;

namespace FitnessApp.Infrastructure.Services;

public class AuthService : IAuthService
{
    private static readonly TimeSpan VerificationLifetime = TimeSpan.FromMinutes(15);
    private static readonly TimeSpan ResendCooldown = TimeSpan.FromSeconds(60);
    private const int MaxVerificationAttempts = 5;

    private readonly ApplicationDbContext _context;
    private readonly IConfiguration _config;
    private readonly IEmailService _emailService;
    private readonly ILogger<AuthService> _logger;

    public AuthService(
        ApplicationDbContext context,
        IConfiguration config,
        IEmailService emailService,
        ILogger<AuthService> logger)
    {
        _context = context;
        _config = config;
        _emailService = emailService;
        _logger = logger;
    }

    public async Task<RegisterResponseDto> RegisterAsync(
     RegisterRequestDto request)
    {
        var normalizedEmail = NormalizeEmail(request.Email);

        if (Encoding.UTF8.GetByteCount(request.Password) > 72)
        {
            throw new InvalidOperationException("Password must not exceed 72 UTF-8 bytes.");
        }

        if (request.DateOfBirth.Date > DateTime.UtcNow.Date)
        {
            throw new InvalidOperationException(
                "Date of birth cannot be in the future.");
        }

        var existingUser = await _context.Users
            .SingleOrDefaultAsync(user => user.Email == normalizedEmail);

        if (existingUser is not null)
        {
            if (!existingUser.IsEmailVerified)
            {
                throw new EmailNotVerifiedException();
            }

            throw new InvalidOperationException(
                "An account with this email already exists. Please sign in.");
        }

        var user = new User
        {
            Email = normalizedEmail,
            PasswordHash = BCrypt.Net.BCrypt.HashPassword(
                request.Password),
            FirstName = request.FirstName.Trim(),
            LastName = request.LastName.Trim(),
            DateOfBirth = request.DateOfBirth.Date,
            Gender = request.Gender.Trim(),
            HeightCm = request.HeightCm,
            IsEmailVerified = false,
            CreatedAt = DateTime.UtcNow
        };

        _context.Users.Add(user);

        try
        {
            await _context.SaveChangesAsync();
        }
        catch (DbUpdateException exception)
            when (IsUniqueViolation(exception))
        {
            throw new InvalidOperationException(
                "An account with this email already exists. Please sign in.");
        }

        await SendVerificationCodeAsync(user, DateTime.UtcNow);

        return new RegisterResponseDto
        {
            Email = normalizedEmail,
            Message =
                "Enter the six-digit code sent to your email to activate your account."
        };
    }


    public async Task<RegisterResponseDto> ResendVerificationAsync(ResendVerificationRequestDto request)
    {
        var normalizedEmail = NormalizeEmail(request.Email);
        var user = await _context.Users.SingleOrDefaultAsync(u => u.Email == normalizedEmail);

        if (user is null || user.IsEmailVerified)
        {
            return new RegisterResponseDto
            {
                Email = normalizedEmail,
                Message = "If an unverified account exists, a verification code will be sent."
            };
        }

        var now = DateTime.UtcNow;
        if (user.EmailVerificationLastSentAtUtc is DateTime lastSentAt && now - lastSentAt < ResendCooldown)
        {
            throw new InvalidOperationException("Wait one minute before requesting another code.");
        }

        await SendVerificationCodeAsync(user, now);
        return new RegisterResponseDto
        {
            Email = normalizedEmail,
            Message = "A new verification code was sent. Check your inbox and spam folder."
        };
    }

    public async Task<AuthResponseDto> VerifyEmailAsync(EmailVerificationRequestDto request)
    {
        var normalizedEmail = NormalizeEmail(request.Email);
        var user = await _context.Users.SingleOrDefaultAsync(u => u.Email == normalizedEmail);

        if (user is null)
        {
            throw new InvalidOperationException("No pending registration was found for this email.");
        }

        if (user.IsEmailVerified)
        {
            throw new InvalidOperationException("This account is already verified. Please sign in.");
        }

        if (user.EmailVerificationAttempts >= MaxVerificationAttempts)
        {
            throw new InvalidOperationException("Too many incorrect codes. Request a new code and try again.");
        }

        if (user.EmailVerificationExpiresAtUtc is not DateTime expiresAt || expiresAt <= DateTime.UtcNow)
        {
            user.EmailVerificationCodeHash = null;
            user.EmailVerificationExpiresAtUtc = null;
            user.EmailVerificationAttempts = 0;
            await _context.SaveChangesAsync();
            throw new InvalidOperationException("This code has expired. Request a new verification code.");
        }

        var expectedHash = user.EmailVerificationCodeHash ?? string.Empty;
        var actualHash = HashVerificationCode(request.Code);
        var expectedBytes = Encoding.UTF8.GetBytes(expectedHash);
        var actualBytes = Encoding.UTF8.GetBytes(actualHash);

        if (expectedBytes.Length != actualBytes.Length ||
            !CryptographicOperations.FixedTimeEquals(expectedBytes, actualBytes))
        {
            user.EmailVerificationAttempts++;
            await _context.SaveChangesAsync();
            throw new InvalidOperationException("That code is not correct. Check the email and try again.");
        }

        user.IsEmailVerified = true;
        user.EmailVerificationCodeHash = null;
        user.EmailVerificationExpiresAtUtc = null;
        user.EmailVerificationLastSentAtUtc = null;
        user.EmailVerificationAttempts = 0;
        await _context.SaveChangesAsync();

        await TrySendWelcomeEmailAsync(user);

        return CreateAuthResponse(user);
    }

    public async Task<AuthResponseDto> LoginAsync(
    LoginRequestDto request)
    {
        if (Encoding.UTF8.GetByteCount(request.Password) > 72)
        {
            throw new UnauthorizedAccessException("Invalid email or password.");
        }

        var normalizedEmail = NormalizeEmail(request.Email);

        var user = await _context.Users
            .SingleOrDefaultAsync(item => item.Email == normalizedEmail);

        if (user is null ||
            !BCrypt.Net.BCrypt.Verify(
                request.Password,
                user.PasswordHash))
        {
            throw new UnauthorizedAccessException(
                "Invalid email or password.");
        }

        if (!user.IsEmailVerified)
        {
            throw new EmailNotVerifiedException();
        }

        return CreateAuthResponse(user);
    }
    private async Task SendVerificationCodeAsync(User user, DateTime now)
    {
        var code = RandomNumberGenerator.GetInt32(0, 1_000_000).ToString("D6");
        user.EmailVerificationCodeHash = HashVerificationCode(code);
        user.EmailVerificationExpiresAtUtc = now.Add(VerificationLifetime);
        user.EmailVerificationLastSentAtUtc = now;
        user.EmailVerificationAttempts = 0;
        await _context.SaveChangesAsync();

        var safeName = HtmlEncoder.Default.Encode(user.FirstName);
        var safeCode = HtmlEncoder.Default.Encode(code);
        var html = $"""
            <div style="font-family:Arial,sans-serif;max-width:560px;margin:auto;color:#15231d">
              <h1>Verify your Kinetix email</h1>
              <p>Hi {safeName}, enter this code in the app to finish creating your account:</p>
              <p style="font-size:32px;font-weight:bold;letter-spacing:8px">{safeCode}</p>
              <p>This code expires in 15 minutes. If you did not request it, you can ignore this message.</p>
            </div>
            """;

        try
        {
            await _emailService.SendEmailAsync(user.Email, "Your Kinetix verification code", html);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "The verification email could not be delivered to user {UserId}.", user.Id);
            user.EmailVerificationCodeHash = null;
            user.EmailVerificationExpiresAtUtc = null;
            user.EmailVerificationLastSentAtUtc = null;
            await _context.SaveChangesAsync();
            throw new EmailDeliveryException("Kinetix could not send the verification email. Check the SMTP configuration and try again.", ex);
        }
    }

    private async Task TrySendWelcomeEmailAsync(User user)
    {
        try
        {
            var firstName = HtmlEncoder.Default.Encode(user.FirstName);
            var brand = HtmlEncoder.Default.Encode(_config["SmtpSettings:SenderName"] ?? "Kinetix");
            var welcomeHtml = $"""
                <div style="font-family:Arial,sans-serif;max-width:560px;margin:auto;color:#15231d">
                  <h1>Welcome to {brand}, {firstName}!</h1>
                  <p><strong>Your account has been created successfully.</strong></p>
                  <p>Your email address is verified. You can now sign in to the Kinetix app and start tracking your workouts, nutrition and movement.</p>
                  <p>Keep moving,<br/>The Kinetix team</p>
                </div>
                """;
            await _emailService.SendEmailAsync(user.Email, "Your Kinetix account has been created", welcomeHtml);
        }
        catch (Exception ex)
        {
            _logger.LogWarning(ex, "The welcome email could not be delivered for user {UserId}.", user.Id);
        }
    }

    private string HashVerificationCode(string code)
    {
        var pepper = _config["JwtSettings:SecretKey"];
        if (string.IsNullOrWhiteSpace(pepper))
        {
            throw new InvalidOperationException("JWT signing key is not configured.");
        }

        using var hmac = new HMACSHA256(Encoding.UTF8.GetBytes(pepper));
        return Convert.ToHexString(hmac.ComputeHash(Encoding.UTF8.GetBytes(code)));
    }

    private AuthResponseDto CreateAuthResponse(User user)
    {
        var jwtKey = _config["JwtSettings:SecretKey"]
            ?? throw new InvalidOperationException("JWT signing key is not configured.");
        var issuer = _config["JwtSettings:Issuer"] ?? "KinetixBackend";
        var audience = _config["JwtSettings:Audience"] ?? "KinetixClients";
        var key = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(jwtKey));
        var credentials = new SigningCredentials(key, SecurityAlgorithms.HmacSha256);
        var claims = new[]
        {
            new Claim(JwtRegisteredClaimNames.Sub, user.Id.ToString()),
            new Claim(JwtRegisteredClaimNames.Email, user.Email),
            new Claim("firstName", user.FirstName),
            new Claim("lastName", user.LastName),
            new Claim(JwtRegisteredClaimNames.Jti, Guid.NewGuid().ToString())
        };
        var token = new JwtSecurityToken(
            issuer: issuer,
            audience: audience,
            claims: claims,
            expires: DateTime.UtcNow.AddDays(7),
            signingCredentials: credentials);

        return new AuthResponseDto
        {
            UserId = user.Id,
            Email = user.Email,
            FirstName = user.FirstName,
            LastName = user.LastName,
            Token = new JwtSecurityTokenHandler().WriteToken(token)
        };
    }

    private static string NormalizeEmail(string email) => email.Trim().ToLowerInvariant();

    private static bool IsUniqueViolation(DbUpdateException exception) =>
        exception.InnerException is Microsoft.Data.SqlClient.SqlException { Number: 2601 or 2627 };

}
