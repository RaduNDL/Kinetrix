using System.Text;
using FitnessApp.Infrastructure.Data;
using FitnessApp.Infrastructure.Services;
using FitnessApp.Application.Interfaces;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddControllers();
builder.Services.AddOpenApi();

var connectionString = builder.Configuration.GetConnectionString("DefaultConnection");
if (string.IsNullOrWhiteSpace(connectionString))
{
    throw new InvalidOperationException("ConnectionStrings:DefaultConnection is missing. Configure it with .NET User Secrets.");
}

builder.Services.AddDbContext<ApplicationDbContext>(options => options.UseSqlServer(connectionString));
builder.Services.AddScoped<IEmailService, EmailService>();
builder.Services.AddScoped<IAuthService, AuthService>();

var jwtKey = builder.Configuration["JwtSettings:SecretKey"];
if (string.IsNullOrWhiteSpace(jwtKey) || Encoding.UTF8.GetByteCount(jwtKey) < 32)
{
    throw new InvalidOperationException("JwtSettings:SecretKey must contain at least 32 bytes. Configure it with .NET User Secrets.");
}

var issuer = builder.Configuration["JwtSettings:Issuer"] ?? "KinetixBackend";
var audience = builder.Configuration["JwtSettings:Audience"] ?? "KinetixClients";
builder.Services.AddAuthentication(JwtBearerDefaults.AuthenticationScheme)
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
            IssuerSigningKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(jwtKey))
        };
    });
builder.Services.AddAuthorization();

var app = builder.Build();

if (app.Environment.IsDevelopment())
{
    app.MapOpenApi();
}

if (!app.Environment.IsDevelopment())
{
    app.UseHttpsRedirection();
}
app.UseAuthentication();
app.UseAuthorization();
app.MapControllers();
app.MapGet("/api/health", async (ApplicationDbContext db) =>
{
    try
    {
        return await db.Database.CanConnectAsync()
            ? Results.Ok(new { api = "ok", database = "ok" })
            : Results.Problem(statusCode: StatusCodes.Status503ServiceUnavailable, title: "Database unavailable");
    }
    catch
    {
        return Results.Problem(statusCode: StatusCodes.Status503ServiceUnavailable, title: "Database unavailable");
    }
});

app.Run();


