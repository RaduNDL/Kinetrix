namespace FitnessApp.Domain.Entities;

public class User
{
    public Guid Id { get; set; } = Guid.NewGuid();
    public string Email { get; set; } = string.Empty;
    public string PasswordHash { get; set; } = string.Empty;
    public string FirstName { get; set; } = string.Empty;
    public string LastName { get; set; } = string.Empty;
    public DateTime DateOfBirth { get; set; }
    public string Gender { get; set; } = string.Empty;
    public decimal HeightCm { get; set; }
    public bool IsEmailVerified { get; set; }
    public string? EmailVerificationCodeHash { get; set; }
    public DateTime? EmailVerificationExpiresAtUtc { get; set; }
    public DateTime? EmailVerificationLastSentAtUtc { get; set; }
    public int EmailVerificationAttempts { get; set; }
    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;

    public ICollection<WorkoutSession> WorkoutSessions { get; set; } = new List<WorkoutSession>();
    public ICollection<MealLog> MealLogs { get; set; } = new List<MealLog>();
    public ICollection<BiometricLog> BiometricLogs { get; set; } = new List<BiometricLog>();
    public ICollection<AdaptivePlan> AdaptivePlans { get; set; } = new List<AdaptivePlan>();
}