namespace FitnessApp.Domain.Entities;

public class BiometricLog
{
    public Guid Id { get; set; } = Guid.NewGuid();
    public Guid UserId { get; set; }
    public User User { get; set; } = null!;
    public DateOnly LogDate { get; set; }
    public decimal WeightKg { get; set; }
    public decimal SleepHours { get; set; }
    public int RestingHeartRate { get; set; }
    public int DailySteps { get; set; }
    public int ActiveCaloriesBurned { get; set; }
}