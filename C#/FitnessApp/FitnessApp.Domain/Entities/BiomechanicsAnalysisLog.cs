namespace FitnessApp.Domain.Entities;

public class BiomechanicsAnalysisLog
{
    public Guid Id { get; set; } = Guid.NewGuid();
    public Guid WorkoutSetId { get; set; }
    public WorkoutSet WorkoutSet { get; set; } = null!;
    public int ValidRepsCount { get; set; }
    public int FailedRepsCount { get; set; }
    public decimal AvgRepDurationSeconds { get; set; }
    public bool SpineFlexionDetected { get; set; }
    public bool KneeValgusDetected { get; set; }
    public decimal MinRecordedJointAngle { get; set; }
    public decimal ConfidenceScore { get; set; }
}