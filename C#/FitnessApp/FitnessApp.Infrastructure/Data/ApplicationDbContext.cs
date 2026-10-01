using Microsoft.EntityFrameworkCore;
using FitnessApp.Domain.Entities;

namespace FitnessApp.Infrastructure.Data;

public class ApplicationDbContext : DbContext
{
    public ApplicationDbContext(DbContextOptions<ApplicationDbContext> options) : base(options)
    {
    }

    protected override void ConfigureConventions(ModelConfigurationBuilder configurationBuilder)
    {
        configurationBuilder.Properties<decimal>().HavePrecision(18, 2);
    }

    public DbSet<User> Users => Set<User>();
    public DbSet<BiometricLog> BiometricLogs => Set<BiometricLog>();
    public DbSet<Exercise> Exercises => Set<Exercise>();
    public DbSet<WorkoutSession> WorkoutSessions => Set<WorkoutSession>();
    public DbSet<WorkoutSet> WorkoutSets => Set<WorkoutSet>();
    public DbSet<BiomechanicsAnalysisLog> BiomechanicsAnalysisLogs => Set<BiomechanicsAnalysisLog>();
    public DbSet<FoodItem> FoodItems => Set<FoodItem>();
    public DbSet<MealLog> MealLogs => Set<MealLog>();
    public DbSet<MealDetail> MealDetails => Set<MealDetail>();
    public DbSet<AdaptivePlan> AdaptivePlans => Set<AdaptivePlan>();
    public DbSet<AdaptiveExerciseTarget> AdaptiveExerciseTargets => Set<AdaptiveExerciseTarget>();

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        base.OnModelCreating(modelBuilder);

        modelBuilder.Entity<User>()
            .HasIndex(u => u.Email)
            .IsUnique();

        // Existing accounts were marked verified in the first email-verification migration.
        modelBuilder.Entity<User>().Property(u => u.IsEmailVerified).HasDefaultValue(false);
        modelBuilder.Entity<User>().Property(u => u.EmailVerificationCodeHash).HasMaxLength(64);

        modelBuilder.Entity<FoodItem>()
            .HasIndex(f => f.Barcode);

        modelBuilder.Entity<WorkoutSet>()
            .HasOne(s => s.BiomechanicsAnalysisLog)
            .WithOne(b => b.WorkoutSet)
            .HasForeignKey<BiomechanicsAnalysisLog>(b => b.WorkoutSetId)
            .OnDelete(DeleteBehavior.Cascade);

        modelBuilder.Entity<AdaptiveExerciseTarget>()
            .HasOne(t => t.Exercise)
            .WithMany(e => e.AdaptiveExerciseTargets)
            .HasForeignKey(t => t.ExerciseId)
            .OnDelete(DeleteBehavior.Restrict);

        modelBuilder.Entity<AdaptiveExerciseTarget>()
            .HasOne(t => t.AdaptivePlan)
            .WithMany(p => p.AdaptiveExerciseTargets)
            .HasForeignKey(t => t.AdaptivePlanId)
            .OnDelete(DeleteBehavior.Cascade);

        modelBuilder.Entity<WorkoutSet>()
            .HasOne(s => s.Exercise)
            .WithMany(e => e.WorkoutSets)
            .HasForeignKey(s => s.ExerciseId)
            .OnDelete(DeleteBehavior.Restrict);

        modelBuilder.Entity<MealDetail>()
            .HasOne(d => d.FoodItem)
            .WithMany(f => f.MealDetails)
            .HasForeignKey(d => d.FoodItemId)
            .OnDelete(DeleteBehavior.Restrict);
    }
}

