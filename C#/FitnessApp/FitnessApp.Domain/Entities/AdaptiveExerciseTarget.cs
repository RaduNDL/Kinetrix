namespace FitnessApp.Domain.Entities;

public class AdaptiveExerciseTarget
{
    public Guid Id { get; set; } = Guid.NewGuid();
    public Guid AdaptivePlanId { get; set; }
    public AdaptivePlan AdaptivePlan { get; set; } = null!;
    public int ExerciseId { get; set; }
    public Exercise Exercise { get; set; } = null!;
    public decimal SuggestedWeightKg { get; set; }
    public int SuggestedSets { get; set; }
    public int SuggestedReps { get; set; }
}