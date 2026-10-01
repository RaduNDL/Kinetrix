namespace FitnessApp.Domain.Entities;

public class WorkoutSession
{
    public Guid Id { get; set; } = Guid.NewGuid();
    public Guid UserId { get; set; }
    public User User { get; set; } = null!;
    public DateTime StartTime { get; set; }
    public DateTime? EndTime { get; set; }
    public decimal SessionRpe { get; set; }
    public string? Notes { get; set; }

    public ICollection<WorkoutSet> WorkoutSets { get; set; } = new List<WorkoutSet>();
}