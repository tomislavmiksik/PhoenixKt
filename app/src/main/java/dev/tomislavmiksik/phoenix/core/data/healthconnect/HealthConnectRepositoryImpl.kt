package dev.tomislavmiksik.phoenix.core.data.healthconnect

import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import dev.tomislavmiksik.phoenix.core.domain.model.HealthSnapshot
import dev.tomislavmiksik.phoenix.core.domain.repository.HealthConnectRepository
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HealthConnectRepositoryImpl @Inject constructor(
    private val clientWrapper: HealthConnectClientWrapper
) : HealthConnectRepository {

    private val client get() = clientWrapper.healthConnectClient

    override suspend fun isAvailable(): Boolean = clientWrapper.isAvailable()

    override suspend fun hasAllPermissions(): Boolean {
        val granted = client.permissionController.getGrantedPermissions()
        return HealthConnectClientWrapper.PERMISSIONS.all { it in granted }
    }

    override fun getRequiredPermissions(): Set<String> = HealthConnectClientWrapper.PERMISSIONS

    override suspend fun getTodaySnapshot(): HealthSnapshot {
        val now = Instant.now()
        val zone = ZoneId.systemDefault()
        val startOfDay = LocalDate.now().atStartOfDay(zone).toInstant()
        val yesterday = now.minusSeconds(24 * 60 * 60)
        val thirtyDaysAgo = now.minusSeconds(30L * 24 * 60 * 60)

        val steps = getStepsInRange(startOfDay, now)
        val sleep = getLatestSleep(yesterday, now)
        val heartRate = getLatestHeartRate(yesterday, now)
        val weight = getLatestWeight(thirtyDaysAgo, now)

        return HealthSnapshot(
            id = 0,
            steps = steps,
            sleepDuration = sleep,
            heartRate = heartRate ?: 0,
            weight = weight ?: 0.0,
            exerciseCount = 0 // TODO: Implement exercise count
        )
    }

    override suspend fun getStepsForDate(date: LocalDate): Long {
        val zone = ZoneId.systemDefault()
        val startOfDay = date.atStartOfDay(zone).toInstant()
        val endOfDay = date.plusDays(1).atStartOfDay(zone).toInstant()
        return getStepsInRange(startOfDay, endOfDay)
    }

    override suspend fun getStepsForDateRange(
        startDate: LocalDate,
        endDate: LocalDate
    ): Map<LocalDate, Long> {
        val zone = ZoneId.systemDefault()
        val result = mutableMapOf<LocalDate, Long>()

        var current = startDate
        while (!current.isAfter(endDate)) {
            result[current] = getStepsForDate(current)
            current = current.plusDays(1)
        }

        return result
    }

    private suspend fun getStepsInRange(startTime: Instant, endTime: Instant): Long {
        val request = ReadRecordsRequest(
            recordType = StepsRecord::class,
            timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
        )
        return client.readRecords(request).records.sumOf { it.count }
    }

    private suspend fun getLatestSleep(startTime: Instant, endTime: Instant): java.time.LocalDateTime? {
        val request = ReadRecordsRequest(
            recordType = SleepSessionRecord::class,
            timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
        )
        val latestSleep = client.readRecords(request).records.maxByOrNull { it.endTime }
        return latestSleep?.let {
            java.time.LocalDateTime.ofInstant(it.endTime, ZoneId.systemDefault())
        }
    }

    private suspend fun getLatestHeartRate(startTime: Instant, endTime: Instant): Long? {
        val request = ReadRecordsRequest(
            recordType = HeartRateRecord::class,
            timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
        )
        return client.readRecords(request).records
            .maxByOrNull { it.endTime }
            ?.samples
            ?.lastOrNull()
            ?.beatsPerMinute
    }

    private suspend fun getLatestWeight(startTime: Instant, endTime: Instant): Double? {
        val request = ReadRecordsRequest(
            recordType = WeightRecord::class,
            timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
        )
        return client.readRecords(request).records
            .maxByOrNull { it.time }
            ?.weight
            ?.inKilograms
    }
}
