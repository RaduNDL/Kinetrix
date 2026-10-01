namespace FitnessApp.Domain.Entities;

public class Exercise
{
    public int Id { get; set; }
    public string Name { get; set; } = string.Empty;
    public string MuscleGroup { get; set; } = string.Empty;
    public string TargetJointPrimary { get; set; } = string.Empty;
    public decimal OptimalAngleMin { get; set; }
    public decimal OptimalAngleMax { get; set; }

    public ICollection<WorkoutSet> WorkoutSets { get; set; } = new List<WorkoutSet>();
    public ICollection<AdaptiveExerciseTarget> AdaptiveExerciseTargets { get; set; } = new List<AdaptiveExerciseTarget>();
}