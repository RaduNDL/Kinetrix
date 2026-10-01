using System.Linq;
using FitnessApp.Application.Interfaces;
using MailKit.Net.Smtp;
using MailKit.Security;
using Microsoft.Extensions.Configuration;
using MimeKit;

namespace FitnessApp.Infrastructure.Services;

public sealed class EmailService : IEmailService
{
    private readonly IConfiguration _configuration;

    public EmailService(IConfiguration configuration)
    {
        _configuration = configuration;
    }

    public async Task SendEmailAsync(
        string toEmail,
        string subject,
        string bodyHtml)
    {
        var server = GetRequiredSetting(
            "SmtpSettings:Server");

        var username = GetRequiredSetting(
            "SmtpSettings:Username");

        var configuredSenderEmail =
            _configuration["SmtpSettings:SenderEmail"];

        var senderEmail = string.IsNullOrWhiteSpace(
            configuredSenderEmail)
            ? username
            : configuredSenderEmail.Trim();

        var senderName =
            _configuration["SmtpSettings:SenderName"]
            ?? "Kinetix Platform";

        var password = GetRequiredSetting(
            "SmtpSettings:Password");

        var portText =
            _configuration["SmtpSettings:Port"]
            ?? "587";

        if (!int.TryParse(portText, out var port))
        {
            throw new InvalidOperationException(
                "SmtpSettings:Port must be a valid number.");
        }

        if (port <= 0 || port > 65535)
        {
            throw new InvalidOperationException(
                "SmtpSettings:Port must be between 1 and 65535.");
        }

        if (string.IsNullOrWhiteSpace(toEmail))
        {
            throw new ArgumentException(
                "Recipient email cannot be empty.",
                nameof(toEmail));
        }

        if (string.IsNullOrWhiteSpace(subject))
        {
            throw new ArgumentException(
                "Email subject cannot be empty.",
                nameof(subject));
        }

        if (string.IsNullOrWhiteSpace(bodyHtml))
        {
            throw new ArgumentException(
                "Email body cannot be empty.",
                nameof(bodyHtml));
        }

        // Google poate afișa App Password-ul cu spații.
        // MailKit trebuie să primească parola fără spații.
        var normalizedPassword = new string(
            password
                .Where(character => !char.IsWhiteSpace(character))
                .ToArray());

        if (string.IsNullOrWhiteSpace(normalizedPassword))
        {
            throw new InvalidOperationException(
                "SmtpSettings:Password cannot be empty.");
        }

        var message = new MimeMessage();

        message.From.Add(
            new MailboxAddress(
                senderName,
                senderEmail));

        message.To.Add(
            MailboxAddress.Parse(toEmail.Trim()));

        message.Subject = subject.Trim();

        message.Body = new BodyBuilder
        {
            HtmlBody = bodyHtml
        }.ToMessageBody();

        using var smtpClient = new SmtpClient();
        using var deliveryTimeout = new CancellationTokenSource(TimeSpan.FromSeconds(20));

        smtpClient.Timeout = 30_000;

        await smtpClient.ConnectAsync(
            server,
            port,
            SecureSocketOptions.StartTls,
            deliveryTimeout.Token);

        await smtpClient.AuthenticateAsync(
            username,
            normalizedPassword,
            deliveryTimeout.Token);

        await smtpClient.SendAsync(message, deliveryTimeout.Token);

        // The SMTP server has accepted the message. A disconnect failure must not
        // invalidate a verification code that was already delivered.
        try
        {
            await smtpClient.DisconnectAsync(true, deliveryTimeout.Token);
        }
        catch (Exception exception) when (exception is IOException or OperationCanceledException or SmtpProtocolException)
        {
        }
    }

    private string GetRequiredSetting(string key)
    {
        var value = _configuration[key];

        if (string.IsNullOrWhiteSpace(value))
        {
            throw new InvalidOperationException(
                $"Missing configuration value: {key}. " +
                "Configure it in Docker .env or .NET User Secrets.");
        }

        return value.Trim();
    }
}
