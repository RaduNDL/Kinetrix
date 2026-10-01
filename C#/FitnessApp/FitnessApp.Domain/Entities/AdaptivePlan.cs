namespace FitnessApp.Domain.Entities;

public class AdaptivePlan
{
    public Guid Id { get; set; } = Guid.NewGuid();
    public Guid UserId { get; set; }
    public User User { get; set; } = null!;
    public DateTime GeneratedAt { get; set; } = DateTime.UtcNow;
    public int TargetDailyCalories { get; set; }
    public decimal TargetProteinGrams { get; set; }
    public decimal TargetCarbsGrams { get; set; }
    public decimal TargetFatsGrams { get; set; }
    public decimal FatigueScore { get; set; }
    public string AdjustmentReason { get; set; } = string.Empty;

    public ICollection<AdaptiveExerciseTarget> AdaptiveExerciseTargets { get; set; } = new List<AdaptiveExerciseTarget>();
}