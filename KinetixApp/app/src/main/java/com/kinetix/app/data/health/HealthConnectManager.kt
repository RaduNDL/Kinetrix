package com.kinetix.app.data.health

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.Record
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.Instant

class HealthConnectManager(
    context: Context
) {
    private val applicationContext =
        context.applicationContext

    val availabilityStatus: Int
        get() = HealthConnectClient.getSdkStatus(
            applicationContext
        )

    val isAvailable: Boolean
        get() = availabilityStatus ==
                HealthConnectClient.SDK_AVAILABLE

    val client: HealthConnectClient?
        get() {
            if (!isAvailable) {
                return null
            }

            return HealthConnectClient.getOrCreate(
                applicationContext
            )
        }

    suspend fun getGrantedPermissions(): Set<String> {
        return client
            ?.permissionController
            ?.getGrantedPermissions()
            ?: emptySet()
    }

    fun getRequiredPermissions(): Set<String> {
        return HealthConnectPermissions.requiredPermissions
    }

    suspend inline fun <reified T : Record> readRecords(
        startTime: Instant,
        endTime: Instant
    ): List<T> {
        val healthConnectClient = client
            ?: return emptyList()

        val response = healthConnectClient.readRecords(
            ReadRecordsRequest(
                recordType = T::class,
                timeRangeFilter = TimeRangeFilter.between(
                    startTime,
                    endTime
                )
            )
        )

        return response.records
    }

    suspend fun hasAllRequiredPermissions(): Boolean {
        val grantedPermissions =
            getGrantedPermissions()

        return grantedPermissions.containsAll(
            HealthConnectPermissions.requiredPermissions
        )
    }
}