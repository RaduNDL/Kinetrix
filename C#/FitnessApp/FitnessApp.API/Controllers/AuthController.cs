using FitnessApp.Application.DTOs;
using FitnessApp.Application.Exceptions;
using FitnessApp.Application.Interfaces;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.RateLimiting;

namespace FitnessApp.API.Controllers;

[ApiController]
[EnableRateLimiting("auth")]
[Route("api/[controller]")]
public class AuthController : ControllerBase
{
    private readonly IAuthService _authService;

    public AuthController(IAuthService authService)
    {
        _authService = authService;
    }

    [HttpPost("register")]
    public async Task<IActionResult> Register(
        [FromBody] RegisterRequestDto request)
    {
        try
        {
            var result = await _authService.RegisterAsync(request);

            return Ok(result);
        }
        catch (EmailNotVerifiedException exception)
        {
            return StatusCode(StatusCodes.Status403Forbidden, new
            {
                message = exception.Message,
                code = "email_verification_required"
            });
        }
        catch (EmailDeliveryException)
        {
            return EmailUnavailable();
        }
        catch (InvalidOperationException exception)
        {
            return BadRequest(new
            {
                message = exception.Message
            });
        }
    }

    [HttpPost("login")]
    public async Task<IActionResult> Login(
        [FromBody] LoginRequestDto request)
    {
        try
        {
            var result = await _authService.LoginAsync(request);

            return Ok(result);
        }
        catch (EmailNotVerifiedException exception)
        {
            return StatusCode(StatusCodes.Status403Forbidden, new
            {
                message = exception.Message,
                code = "email_verification_required"
            });
        }
        catch (UnauthorizedAccessException exception)
        {
            return Unauthorized(new
            {
                message = exception.Message
            });
        }
    }

    [HttpPost("verify-email")]
    public async Task<IActionResult> VerifyEmail([FromBody] EmailVerificationRequestDto request)
    {
        try
        {
            return Ok(await _authService.VerifyEmailAsync(request));
        }
        catch (InvalidOperationException exception)
        {
            return BadRequest(new { message = exception.Message });
        }
    }

    [HttpPost("resend-verification")]
    public async Task<IActionResult> ResendVerification([FromBody] ResendVerificationRequestDto request)
    {
        try
        {
            return Ok(await _authService.ResendVerificationAsync(request));
        }
        catch (EmailDeliveryException)
        {
            return EmailUnavailable();
        }
        catch (InvalidOperationException exception)
        {
            return BadRequest(new { message = exception.Message });
        }
    }

    private ObjectResult EmailUnavailable() =>
        StatusCode(StatusCodes.Status503ServiceUnavailable, new
        {
            message = "Your account is pending verification, but the email could not be sent. Please request a new code shortly.",
            code = "email_delivery_failed"
        });
}
