package dev.tomislavmiksik.phoenix.core.data.healthconnect

import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.FloorsClimbedRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import dev.tomislavmiksik.phoenix.core.data.local.HealthSnapshotDao
import dev.tomislavmiksik.phoenix.core.domain.model.HealthSnapshot
import dev.tomislavmiksik.phoenix.core.domain.repository.HealthConnectRepository
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HealthConnectRepositoryImpl @Inject constructor(
    private val clientWrapper: HealthConnectClientWrapper,
    private val healthSnapshotDao: HealthSnapshotDao,
) : HealthConnectRepository {

    private val client get() = clientWrapper.healthConnectClient
    private val zone get() = ZoneId.systemDefault()
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    override suspend fun isAvailable(): Boolean = clientWrapper.isAvailable()

    override suspend fun hasAllPermissions(): Boolean {
        val granted = client.permissionController.getGrantedPermissions()
        return HealthConnectClientWrapper.PERMISSIONS.all { it in granted }
    }

    override fun getRequiredPermissions(): Set<String> = HealthConnectClientWrapper.PERMISSIONS

    override suspend fun getTodaySnapshot(): HealthSnapshot {
        val now = Instant.now()
        val startOfDay = LocalDate.now().atStartOfDay(zone).toInstant()
        val yesterday = now.minusSeconds(24 * 60 * 60)
        val thirtyDaysAgo = now.minusSeconds(30L * 24 * 60 * 60)

        // Activity data (today)
        val steps = getStepsInRange(startOfDay, now)
        val distance = getDistanceInRange(startOfDay, now)
        val activeCalories = getActiveCaloriesInRange(startOfDay, now)
        val totalCalories = getTotalCaloriesInRange(startOfDay, now)
        val floors = getFloorsInRange(startOfDay, now)
        val exercises = getExercisesInRange(startOfDay, now)

        // Sleep data (last night)
        val sleep = getLatestSleep(yesterday, now)

        // Vitals
        val heartRate = getLatestHeartRate(yesterday, now)
        val weight = getLatestWeight(thirtyDaysAgo, now)

        return HealthSnapshot(
            date = LocalDate.now(),
            // Activity
            steps = steps,
            distanceMeters = distance,
            activeCalories = activeCalories,
            totalCalories = totalCalories,
            floorsClimbed = floors,
            exerciseCount = exercises.size,
            exerciseDurationMinutes = exercises.sumOf {
                Duration.between(it.startTime, it.endTime).toMinutes()
            },
            // Sleep
            sleepDurationMinutes = sleep?.durationMinutes ?: 0,
            sleepStartTime = sleep?.startTime,
            sleepEndTime = sleep?.endTime,
            // Vitals
            heartRate = heartRate ?: 0,
            weight = weight ?: 0.0
        )
    }

    override suspend fun getStepsForDate(date: LocalDate): Long {
        val startOfDay = date.atStartOfDay(zone).toInstant()
        val endOfDay = date.plusDays(1).atStartOfDay(zone).toInstant()
        return getStepsInRange(startOfDay, endOfDay)
    }

    override suspend fun getStepsForDateRange(
        startDate: LocalDate,
        endDate: LocalDate,
    ): Map<LocalDate, Long> {
        val result = mutableMapOf<LocalDate, Long>()
        var current = startDate
        while (!current.isAfter(endDate)) {
            result[current] = getStepsForDate(current)
            current = current.plusDays(1)
        }
        return result
    }

    override suspend fun getActiveCaloriesForDate(date: LocalDate): Long {
        val startOfDay = date.atStartOfDay(zone).toInstant()
        val endOfDay = date.plusDays(1).atStartOfDay(zone).toInstant()
        return getTotalCaloriesInRange(startOfDay, endOfDay).toLong()
    }

    override suspend fun getActiveCaloriesForDateRange(
        startDate: LocalDate,
        endDate: LocalDate,
    ): Map<LocalDate, Long> {
        val result = mutableMapOf<LocalDate, Long>()
        var current = startDate
        while (!current.isAfter(endDate)) {
            result[current] = getActiveCaloriesForDate(current)
            current = current.plusDays(1)
        }
        return result
    }

    // Activity fetchers

    private suspend fun getStepsInRange(startTime: Instant, endTime: Instant): Long {
        val request = ReadRecordsRequest(
            recordType = StepsRecord::class,
            timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
        )
        return client.readRecords(request).records.sumOf { it.count }
    }

    private suspend fun getDistanceInRange(startTime: Instant, endTime: Instant): Double {
        val request = ReadRecordsRequest(
            recordType = DistanceRecord::class,
            timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
        )
        return client.readRecords(request).records.sumOf { it.distance.inMeters }
    }

    private suspend fun getActiveCaloriesInRange(startTime: Instant, endTime: Instant): Double {
        val request = ReadRecordsRequest(
            recordType = ActiveCaloriesBurnedRecord::class,
            timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
        )
        return client.readRecords(request).records.sumOf { it.energy.inKilocalories }
    }

    private suspend fun getTotalCaloriesInRange(startTime: Instant, endTime: Instant): Double {
        val request = ReadRecordsRequest(
            recordType = TotalCaloriesBurnedRecord::class,
            timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
        )
        return client.readRecords(request).records.sumOf { it.energy.inKilocalories }
    }

    private suspend fun getFloorsInRange(startTime: Instant, endTime: Instant): Long {
        val request = ReadRecordsRequest(
            recordType = FloorsClimbedRecord::class,
            timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
        )
        return client.readRecords(request).records.sumOf { it.floors.toLong() }
    }

    private suspend fun getExercisesInRange(
        startTime: Instant,
        endTime: Instant,
    ): List<ExerciseSessionRecord> {
        val request = ReadRecordsRequest(
            recordType = ExerciseSessionRecord::class,
            timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
        )
        return client.readRecords(request).records
    }

    // Sleep fetcher

    private data class SleepData(
        val durationMinutes: Long,
        val startTime: String,
        val endTime: String,
    )

    private suspend fun getLatestSleep(startTime: Instant, endTime: Instant): SleepData? {
        val request = ReadRecordsRequest(
            recordType = SleepSessionRecord::class,
            timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
        )
        val latestSleep = client.readRecords(request).records.maxByOrNull { it.endTime }
        return latestSleep?.let {
            val duration = Duration.between(it.startTime, it.endTime).toMinutes()
            val start = LocalTime.ofInstant(it.startTime, zone).format(timeFormatter)
            val end = LocalTime.ofInstant(it.endTime, zone).format(timeFormatter)
            SleepData(duration, start, end)
        }
    }

    // Vitals fetchers

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
