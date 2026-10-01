using System.ComponentModel.DataAnnotations;

namespace FitnessApp.Application.DTOs;

public class EmailVerificationRequestDto
{
    [Required, EmailAddress, StringLength(254)]
    public string Email { get; set; } = string.Empty;

    [Required, RegularExpression("^\\d{6}$")]
    public string Code { get; set; } = string.Empty;
}

public class ResendVerificationRequestDto
{
    [Required, EmailAddress, StringLength(254)]
    public string Email { get; set; } = string.Empty;
}
