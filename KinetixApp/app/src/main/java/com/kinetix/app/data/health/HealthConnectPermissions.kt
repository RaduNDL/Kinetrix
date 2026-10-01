package com.kinetix.app.data.health

import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.RestingHeartRateRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.WeightRecord

object HealthConnectPermissions {

    val requiredPermissions: Set<String> = setOf(
        HealthPermission.getReadPermission(
            StepsRecord::class
        ),
        HealthPermission.getReadPermission(
            ActiveCaloriesBurnedRecord::class
        ),
        HealthPermission.getReadPermission(
            SleepSessionRecord::class
        ),
        HealthPermission.getReadPermission(
            RestingHeartRateRecord::class
        ),
        HealthPermission.getReadPermission(
            HeartRateRecord::class
        ),
        HealthPermission.getReadPermission(
            WeightRecord::class
        )
    )
}