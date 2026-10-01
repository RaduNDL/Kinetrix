using System;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace FitnessApp.Infrastructure.Migrations
{
    /// <inheritdoc />
    public partial class InitialCreate : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.CreateTable(
                name: "Exercises",
                columns: table => new
                {
                    Id = table.Column<int>(type: "int", nullable: false)
                        .Annotation("SqlServer:Identity", "1, 1"),
                    Name = table.Column<string>(type: "nvarchar(max)", nullable: false),
                    MuscleGroup = table.Column<string>(type: "nvarchar(max)", nullable: false),
                    TargetJointPrimary = table.Column<string>(type: "nvarchar(max)", nullable: false),
                    OptimalAngleMin = table.Column<decimal>(type: "decimal(18,2)", nullable: false),
                    OptimalAngleMax = table.Column<decimal>(type: "decimal(18,2)", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Exercises", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "FoodItems",
                columns: table => new
                {
                    Id = table.Column<int>(type: "int", nullable: false)
                        .Annotation("SqlServer:Identity", "1, 1"),
                    Barcode = table.Column<string>(type: "nvarchar(450)", nullable: true),
                    Name = table.Column<string>(type: "nvarchar(max)", nullable: false),
                    ServingSizeGrams = table.Column<decimal>(type: "decimal(18,2)", nullable: false),
                    Calories = table.Column<decimal>(type: "decimal(18,2)", nullable: false),
                    ProteinGrams = table.Column<decimal>(type: "decimal(18,2)", nullable: false),
                    CarbsGrams = table.Column<decimal>(type: "decimal(18,2)", nullable: false),
                    FatsGrams = table.Column<decimal>(type: "decimal(18,2)", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_FoodItems", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "Users",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    Email = table.Column<string>(type: "nvarchar(450)", nullable: false),
                    PasswordHash = table.Column<string>(type: "nvarchar(max)", nullable: false),
                    FirstName = table.Column<string>(type: "nvarchar(max)", nullable: false),
                    LastName = table.Column<string>(type: "nvarchar(max)", nullable: false),
                    DateOfBirth = table.Column<DateTime>(type: "datetime2", nullable: false),
                    Gender = table.Column<string>(type: "nvarchar(max)", nullable: false),
                    HeightCm = table.Column<decimal>(type: "decimal(18,2)", nullable: false),
                    CreatedAt = table.Column<DateTime>(type: "datetime2", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Users", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "AdaptivePlans",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    UserId = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    GeneratedAt = table.Column<DateTime>(type: "datetime2", nullable: false),
                    TargetDailyCalories = table.Column<int>(type: "int", nullable: false),
                    TargetProteinGrams = table.Column<decimal>(type: "decimal(18,2)", nullable: false),
                    TargetCarbsGrams = table.Column<decimal>(type: "decimal(18,2)", nullable: false),
                    TargetFatsGrams = table.Column<decimal>(type: "decimal(18,2)", nullable: false),
                    FatigueScore = table.Column<decimal>(type: "decimal(18,2)", nullable: false),
                    AdjustmentReason = table.Column<string>(type: "nvarchar(max)", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_AdaptivePlans", x => x.Id);
                    table.ForeignKey(
                        name: "FK_AdaptivePlans_Users_UserId",
                        column: x => x.UserId,
                        principalTable: "Users",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "BiometricLogs",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    UserId = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    LogDate = table.Column<DateOnly>(type: "date", nullable: false),
                    WeightKg = table.Column<decimal>(type: "decimal(18,2)", nullable: false),
                    SleepHours = table.Column<decimal>(type: "decimal(18,2)", nullable: false),
                    RestingHeartRate = table.Column<int>(type: "int", nullable: false),
                    DailySteps = table.Column<int>(type: "int", nullable: false),
                    ActiveCaloriesBurned = table.Column<int>(type: "int", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_BiometricLogs", x => x.Id);
                    table.ForeignKey(
                        name: "FK_BiometricLogs_Users_UserId",
                        column: x => x.UserId,
                        principalTable: "Users",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "MealLogs",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    UserId = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    ConsumedAt = table.Column<DateTime>(type: "datetime2", nullable: false),
                    MealType = table.Column<string>(type: "nvarchar(max)", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_MealLogs", x => x.Id);
                    table.ForeignKey(
                        name: "FK_MealLogs_Users_UserId",
                        column: x => x.UserId,
                        principalTable: "Users",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "WorkoutSessions",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    UserId = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    StartTime = table.Column<DateTime>(type: "datetime2", nullable: false),
                    EndTime = table.Column<DateTime>(type: "datetime2", nullable: true),
                    SessionRpe = table.Column<decimal>(type: "decimal(18,2)", nullable: false),
                    Notes = table.Column<string>(type: "nvarchar(max)", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_WorkoutSessions", x => x.Id);
                    table.ForeignKey(
                        name: "FK_WorkoutSessions_Users_UserId",
                        column: x => x.UserId,
                        principalTable: "Users",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "AdaptiveExerciseTargets",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    AdaptivePlanId = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    ExerciseId = table.Column<int>(type: "int", nullable: false),
                    SuggestedWeightKg = table.Column<decimal>(type: "decimal(18,2)", nullable: false),
                    SuggestedSets = table.Column<int>(type: "int", nullable: false),
                    SuggestedReps = table.Column<int>(type: "int", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_AdaptiveExerciseTargets", x => x.Id);
                    table.ForeignKey(
                        name: "FK_AdaptiveExerciseTargets_AdaptivePlans_AdaptivePlanId",
                        column: x => x.AdaptivePlanId,
                        principalTable: "AdaptivePlans",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_AdaptiveExerciseTargets_Exercises_ExerciseId",
                        column: x => x.ExerciseId,
                        principalTable: "Exercises",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Restrict);
                });

            migrationBuilder.CreateTable(
                name: "MealDetails",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    MealLogId = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    FoodItemId = table.Column<int>(type: "int", nullable: false),
                    QuantityGrams = table.Column<decimal>(type: "decimal(18,2)", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_MealDetails", x => x.Id);
                    table.ForeignKey(
                        name: "FK_MealDetails_FoodItems_FoodItemId",
                        column: x => x.FoodItemId,
                        principalTable: "FoodItems",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Restrict);
                    table.ForeignKey(
                        name: "FK_MealDetails_MealLogs_MealLogId",
                        column: x => x.MealLogId,
                        principalTable: "MealLogs",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "WorkoutSets",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    WorkoutSessionId = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    ExerciseId = table.Column<int>(type: "int", nullable: false),
                    SetNumber = table.Column<int>(type: "int", nullable: false),
                    WeightKg = table.Column<decimal>(type: "decimal(18,2)", nullable: false),
                    RepsCompleted = table.Column<int>(type: "int", nullable: false),
                    Rpe = table.Column<decimal>(type: "decimal(18,2)", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_WorkoutSets", x => x.Id);
                    table.ForeignKey(
                        name: "FK_WorkoutSets_Exercises_ExerciseId",
                        column: x => x.ExerciseId,
                        principalTable: "Exercises",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Restrict);
                    table.ForeignKey(
                        name: "FK_WorkoutSets_WorkoutSessions_WorkoutSessionId",
                        column: x => x.WorkoutSessionId,
                        principalTable: "WorkoutSessions",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "BiomechanicsAnalysisLogs",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    WorkoutSetId = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    ValidRepsCount = table.Column<int>(type: "int", nullable: false),
                    FailedRepsCount = table.Column<int>(type: "int", nullable: false),
                    AvgRepDurationSeconds = table.Column<decimal>(type: "decimal(18,2)", nullable: false),
                    SpineFlexionDetected = table.Column<bool>(type: "bit", nullable: false),
                    KneeValgusDetected = table.Column<bool>(type: "bit", nullable: false),
                    MinRecordedJointAngle = table.Column<decimal>(type: "decimal(18,2)", nullable: false),
                    ConfidenceScore = table.Column<decimal>(type: "decimal(18,2)", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_BiomechanicsAnalysisLogs", x => x.Id);
                    table.ForeignKey(
                        name: "FK_BiomechanicsAnalysisLogs_WorkoutSets_WorkoutSetId",
                        column: x => x.WorkoutSetId,
                        principalTable: "WorkoutSets",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateIndex(
                name: "IX_AdaptiveExerciseTargets_AdaptivePlanId",
                table: "AdaptiveExerciseTargets",
                column: "AdaptivePlanId");

            migrationBuilder.CreateIndex(
                name: "IX_AdaptiveExerciseTargets_ExerciseId",
                table: "AdaptiveExerciseTargets",
                column: "ExerciseId");

            migrationBuilder.CreateIndex(
                name: "IX_AdaptivePlans_UserId",
                table: "AdaptivePlans",
                column: "UserId");

            migrationBuilder.CreateIndex(
                name: "IX_BiomechanicsAnalysisLogs_WorkoutSetId",
                table: "BiomechanicsAnalysisLogs",
                column: "WorkoutSetId",
                unique: true);

            migrationBuilder.CreateIndex(
                name: "IX_BiometricLogs_UserId",
                table: "BiometricLogs",
                column: "UserId");

            migrationBuilder.CreateIndex(
                name: "IX_FoodItems_Barcode",
                table: "FoodItems",
                column: "Barcode");

            migrationBuilder.CreateIndex(
                name: "IX_MealDetails_FoodItemId",
                table: "MealDetails",
                column: "FoodItemId");

            migrationBuilder.CreateIndex(
                name: "IX_MealDetails_MealLogId",
                table: "MealDetails",
                column: "MealLogId");

            migrationBuilder.CreateIndex(
                name: "IX_MealLogs_UserId",
                table: "MealLogs",
                column: "UserId");

            migrationBuilder.CreateIndex(
                name: "IX_Users_Email",
                table: "Users",
                column: "Email",
                unique: true);

            migrationBuilder.CreateIndex(
                name: "IX_WorkoutSessions_UserId",
                table: "WorkoutSessions",
                column: "UserId");

            migrationBuilder.CreateIndex(
                name: "IX_WorkoutSets_ExerciseId",
                table: "WorkoutSets",
                column: "ExerciseId");

            migrationBuilder.CreateIndex(
                name: "IX_WorkoutSets_WorkoutSessionId",
                table: "WorkoutSets",
                column: "WorkoutSessionId");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "AdaptiveExerciseTargets");

            migrationBuilder.DropTable(
                name: "BiomechanicsAnalysisLogs");

            migrationBuilder.DropTable(
                name: "BiometricLogs");

            migrationBuilder.DropTable(
                name: "MealDetails");

            migrationBuilder.DropTable(
                name: "AdaptivePlans");

            migrationBuilder.DropTable(
                name: "WorkoutSets");

            migrationBuilder.DropTable(
                name: "FoodItems");

            migrationBuilder.DropTable(
                name: "MealLogs");

            migrationBuilder.DropTable(
                name: "Exercises");

            migrationBuilder.DropTable(
                name: "WorkoutSessions");

            migrationBuilder.DropTable(
                name: "Users");
        }
    }
}
