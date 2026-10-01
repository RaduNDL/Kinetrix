namespace FitnessApp.Domain.Entities;

public class MealLog
{
    public Guid Id { get; set; } = Guid.NewGuid();
    public Guid UserId { get; set; }
    public User User { get; set; } = null!;
    public DateTime ConsumedAt { get; set; }
    public string MealType { get; set; } = string.Empty;

    public ICollection<MealDetail> MealDetails { get; set; } = new List<MealDetail>();
}