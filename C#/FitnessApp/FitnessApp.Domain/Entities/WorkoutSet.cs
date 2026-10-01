namespace FitnessApp.Domain.Entities;

public class WorkoutSet
{
    public Guid Id { get; set; } = Guid.NewGuid();
    public Guid WorkoutSessionId { get; set; }
    public WorkoutSession WorkoutSession { get; set; } = null!;
    public int ExerciseId { get; set; }
    public Exercise Exercise { get; set; } = null!;
    public int SetNumber { get; set; }
    public decimal WeightKg { get; set; }
    public int RepsCompleted { get; set; }
    public decimal Rpe { get; set; }

    public BiomechanicsAnalysisLog? BiomechanicsAnalysisLog { get; set; }
}