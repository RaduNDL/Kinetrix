namespace FitnessApp.Domain.Entities;

public class FoodItem
{
    public int Id { get; set; }
    public string? Barcode { get; set; }
    public string Name { get; set; } = string.Empty;
    public decimal ServingSizeGrams { get; set; }
    public decimal Calories { get; set; }
    public decimal ProteinGrams { get; set; }
    public decimal CarbsGrams { get; set; }
    public decimal FatsGrams { get; set; }

    public ICollection<MealDetail> MealDetails { get; set; } = new List<MealDetail>();
}