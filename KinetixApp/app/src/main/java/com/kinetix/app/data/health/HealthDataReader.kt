package com.kinetix.app.data.health

import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.RestingHeartRateRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.WeightRecord
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

data class HealthDataSummary(
    val date: LocalDate,
    val steps: Long = 0L,
    val activeCalories: Double = 0.0,
    val sleepHours: Double = 0.0,
    val restingHeartRate: Int? = null,
    val averageHeartRate: Double? = null,
    val weightKg: Double? = null
)

class HealthDataReader(
    private val healthConnectManager: HealthConnectManager
) {
    suspend fun readToday(): HealthDataSummary {
        val zoneId = ZoneId.systemDefault()
        val today = LocalDate.now(zoneId)

        val startOfDay = today
            .atStartOfDay(zoneId)
            .toInstant()

        val endOfDay = today
            .plusDays(1)
            .atStartOfDay(zoneId)
            .toInstant()

        return readForDate(
            date = today,
            startTime = startOfDay,
            endTime = endOfDay
        )
    }

    suspend fun readForDate(
        date: LocalDate,
        startTime: Instant,
        endTime: Instant
    ): HealthDataSummary {
        if (!healthConnectManager.isAvailable) {
            return HealthDataSummary(date = date)
        }

        if (
            !healthConnectManager
                .hasAllRequiredPermissions()
        ) {
            return HealthDataSummary(date = date)
        }

        return HealthDataSummary(
            date = date,
            steps = readSteps(startTime, endTime),
            activeCalories = readActiveCalories(
                startTime,
                endTime
            ),
            sleepHours = readSleepHours(
                startTime,
                endTime
            ),
            restingHeartRate = readRestingHeartRate(
                startTime,
                endTime
            ),
            averageHeartRate = readAverageHeartRate(
                startTime,
                endTime
            ),
            weightKg = readLatestWeight(
                startTime,
                endTime
            )
        )
    }

    private suspend fun readSteps(
        startTime: Instant,
        endTime: Instant
    ): Long {
        val records =
            healthConnectManager.readRecords<StepsRecord>(
                startTime,
                endTime
            )

        return records.sumOf { record ->
            record.count.toLong()
        }
    }

    private suspend fun readActiveCalories(
        startTime: Instant,
        endTime: Instant
    ): Double {
        val records =
            healthConnectManager
                .readRecords<ActiveCaloriesBurnedRecord>(
                    startTime,
                    endTime
                )

        return records.sumOf { record ->
            record.energy.inKilocalories
        }
    }

    private suspend fun readSleepHours(
        startTime: Instant,
        endTime: Instant
    ): Double {
        val records =
            healthConnectManager
                .readRecords<SleepSessionRecord>(
                    startTime,
                    endTime
                )

        val totalMinutes = records.sumOf { record ->
            Duration.between(
                record.startTime,
                record.endTime
            ).toMinutes()
        }

        return totalMinutes / 60.0
    }

    private suspend fun readRestingHeartRate(
        startTime: Instant,
        endTime: Instant
    ): Int? {
        val records =
            healthConnectManager
                .readRecords<RestingHeartRateRecord>(
                    startTime,
                    endTime
                )

        return records
            .maxByOrNull { record ->
                record.time
            }
            ?.beatsPerMinute
            ?.toInt()
    }

    private suspend fun readAverageHeartRate(
        startTime: Instant,
        endTime: Instant
    ): Double? {
        val records =
            healthConnectManager
                .readRecords<HeartRateRecord>(
                    startTime,
                    endTime
                )

        val samples = records.flatMap { record ->
            record.samples
        }

        if (samples.isEmpty()) {
            return null
        }

        return samples
            .map { sample ->
                sample.beatsPerMinute.toDouble()
            }
            .average()
    }

    private suspend fun readLatestWeight(
        startTime: Instant,
        endTime: Instant
    ): Double? {
        val records =
            healthConnectManager
                .readRecords<WeightRecord>(
                    startTime,
                    endTime
                )

        return records
            .maxByOrNull { record ->
                record.time
            }
            ?.weight
            ?.inKilograms
    }
}