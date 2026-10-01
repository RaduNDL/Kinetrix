using System.Text;
using FitnessApp.Application.Interfaces;
using FitnessApp.Infrastructure.Data;
using FitnessApp.Infrastructure.Services;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;
using System.Threading.RateLimiting;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddControllers();
builder.Services.AddOpenApi();
builder.Services.AddRateLimiter(options =>
{
    options.RejectionStatusCode = StatusCodes.Status429TooManyRequests;
    options.AddPolicy("auth", context => RateLimitPartition.GetFixedWindowLimiter(
        context.Connection.RemoteIpAddress?.ToString() ?? "unknown",
        _ => new FixedWindowRateLimiterOptions
        {
            PermitLimit = 30,
            Window = TimeSpan.FromMinutes(1),
            QueueLimit = 0
        }));
});

var connectionString = builder.Configuration.GetConnectionString("DefaultConnection");

if (string.IsNullOrWhiteSpace(connectionString))
{
    throw new InvalidOperationException(
        "ConnectionStrings:DefaultConnection is missing.");
}

builder.Services.AddDbContext<ApplicationDbContext>(options =>
    options.UseSqlServer(
        connectionString,
        sqlOptions =>
        {
            sqlOptions.MigrationsAssembly(
                typeof(ApplicationDbContext).Assembly.FullName);
        }));

builder.Services.AddScoped<IEmailService, EmailService>();
builder.Services.AddScoped<IAuthService, AuthService>();

var jwtKey = builder.Configuration["JwtSettings:SecretKey"];

if (string.IsNullOrWhiteSpace(jwtKey) ||
    Encoding.UTF8.GetByteCount(jwtKey) < 32)
{
    throw new InvalidOperationException(
        "JwtSettings:SecretKey must contain at least 32 bytes.");
}

var issuer =
    builder.Configuration["JwtSettings:Issuer"]
    ?? "KinetixBackend";

var audience =
    builder.Configuration["JwtSettings:Audience"]
    ?? "KinetixClients";

builder.Services
    .AddAuthentication(JwtBearerDefaults.AuthenticationScheme)
    .AddJwtBearer(options =>
    {
        options.TokenValidationParameters = new TokenValidationParameters
        {
            ValidateIssuer = true,
            ValidateAudience = true,
            ValidateLifetime = true,
            ValidateIssuerSigningKey = true,
            ValidIssuer = issuer,
            ValidAudience = audience,
            IssuerSigningKey = new SymmetricSecurityKey(
                Encoding.UTF8.GetBytes(jwtKey))
        };
    });

builder.Services.AddAuthorization();

var app = builder.Build();

const int maximumDatabaseAttempts = 30;
const int retryDelaySeconds = 2;

using (var scope = app.Services.CreateScope())
{
    var database = scope.ServiceProvider
        .GetRequiredService<ApplicationDbContext>();

    var logger = scope.ServiceProvider
        .GetRequiredService<ILogger<Program>>();

    var databaseReady = false;

    for (var attempt = 1;
         attempt <= maximumDatabaseAttempts;
         attempt++)
    {
        try
        {
            logger.LogInformation(
                "Waiting for SQL Server and applying EF Core migrations. Attempt {Attempt}/{MaximumAttempts}.",
                attempt,
                maximumDatabaseAttempts);

            await database.Database.MigrateAsync();

            databaseReady = true;

            logger.LogInformation(
                "SQL Server is available and EF Core migrations were applied successfully.");

            break;
        }
        catch (Exception exception)
        {
            logger.LogWarning(
                exception,
                "Database initialization failed on attempt {Attempt}/{MaximumAttempts}.",
                attempt,
                maximumDatabaseAttempts);

            if (attempt == maximumDatabaseAttempts)
            {
                logger.LogCritical(
                    exception,
                    "Database initialization failed after {MaximumAttempts} attempts.",
                    maximumDatabaseAttempts);

                throw;
            }

            await Task.Delay(
                TimeSpan.FromSeconds(retryDelaySeconds));
        }
    }

    if (!databaseReady)
    {
        throw new InvalidOperationException(
            "The database could not be initialized.");
    }
}

if (app.Environment.IsDevelopment())
{
    app.MapOpenApi();
}

if (!app.Environment.IsDevelopment())
{
    app.UseHttpsRedirection();
}

app.UseAuthentication();
app.UseRateLimiter();
app.UseAuthorization();

app.MapControllers();

app.MapGet("/api/health", async (ApplicationDbContext database) =>
{
    try
    {
        var canConnect = await database.Database.CanConnectAsync();

        return canConnect
            ? Results.Ok(new
            {
                api = "ok",
                database = "ok"
            })
            : Results.Problem(
                statusCode: StatusCodes.Status503ServiceUnavailable,
                title: "Database unavailable");
    }
    catch
    {
        return Results.Problem(
            statusCode: StatusCodes.Status503ServiceUnavailable,
            title: "Database unavailable");
    }
});

app.Run();
