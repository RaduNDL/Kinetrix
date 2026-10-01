package com.kinetix.app.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(
    tableName = "daily_health_summary"
)
data class DailyHealthSummaryEntity(
    @PrimaryKey
    val date: String,
    val steps: Long,
    val activeCalories: Double,
    val sleepHours: Double,
    val restingHeartRate: Int?,
    val weightKg: Double?,
    val updatedAtEpochMillis: Long
)

@Dao
interface DailyHealthSummaryDao {

    @Query(
        """
        SELECT * FROM daily_health_summary
        WHERE date = :date
        LIMIT 1
        """
    )
    fun observeByDate(
        date: String
    ): Flow<DailyHealthSummaryEntity?>

    @Query(
        """
        SELECT * FROM daily_health_summary
        WHERE date = :date
        LIMIT 1
        """
    )
    suspend fun getByDate(
        date: String
    ): DailyHealthSummaryEntity?

    @Insert(
        onConflict = OnConflictStrategy.REPLACE
    )
    suspend fun insert(
        summary: DailyHealthSummaryEntity
    )

    @Query(
        """
        DELETE FROM daily_health_summary
        WHERE date = :date
        """
    )
    suspend fun deleteByDate(
        date: String
    )
}

@Database(
    entities = [
        DailyHealthSummaryEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun dailyHealthSummaryDao():
            DailyHealthSummaryDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(
            context: Context
        ): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "kinetix_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { database ->
                        instance = database
                    }
            }
        }
    }
}