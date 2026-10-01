namespace FitnessApp.Domain.Entities;

public class MealDetail
{
    public Guid Id { get; set; } = Guid.NewGuid();
    public Guid MealLogId { get; set; }
    public MealLog MealLog { get; set; } = null!;
    public int FoodItemId { get; set; }
    public FoodItem FoodItem { get; set; } = null!;
    public decimal QuantityGrams { get; set; }
}