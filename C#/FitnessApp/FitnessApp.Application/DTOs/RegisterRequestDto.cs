using System.ComponentModel.DataAnnotations;

namespace FitnessApp.Application.DTOs;

public class RegisterRequestDto
{
    [Required, EmailAddress, StringLength(254)]
    public string Email { get; set; } = string.Empty;

    [Required, MinLength(10), MaxLength(72)]
    public string Password { get; set; } = string.Empty;

    [Required, StringLength(60)]
    public string FirstName { get; set; } = string.Empty;

    [Required, StringLength(60)]
    public string LastName { get; set; } = string.Empty;

    [Required]
    public DateTime DateOfBirth { get; set; }

    [Required, StringLength(40)]
    public string Gender { get; set; } = string.Empty;

    [Range(typeof(decimal), "80", "250")]
    public decimal HeightCm { get; set; }
}
