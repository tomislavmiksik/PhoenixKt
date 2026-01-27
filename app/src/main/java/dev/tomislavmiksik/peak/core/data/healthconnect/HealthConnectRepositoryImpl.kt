package dev.tomislavmiksik.peak.core.data.healthconnect

import android.util.Log
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.FloorsClimbedRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.request.AggregateGroupByPeriodRequest
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import dev.tomislavmiksik.peak.core.data.local.entity.HealthSnapshot
import dev.tomislavmiksik.peak.core.domain.repository.HealthConnectRepository
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.Period
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Health connect repository impl
 *
 * @property clientWrapper
 * @constructor Create empty Health connect repository impl
 */
@Singleton
class HealthConnectRepositoryImpl @Inject constructor(
    private val clientWrapper: HealthConnectClientWrapper,
) : HealthConnectRepository {

    private suspend fun <T> safeRead(default: T, block: suspend () -> T): T {
        return try {
            block()
        } catch (e: Exception) {
            Log.e("HealthConnect", "Read failed", e)
            default
        }
    }

    private data class ActivityAggregates(
        val steps: Long = 0,
        val distance: Double = 0.0,
        val activeCalories: Double = 0.0,
        val totalCalories: Double = 0.0,
        val floors: Long = 0,
    )

    private val client get() = clientWrapper.healthConnectClient
    private val zone get() = ZoneId.systemDefault()
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")


    //region Permissions
    override suspend fun isAvailable(): Boolean = clientWrapper.isAvailable()

    override suspend fun hasAllPermissions(): Boolean {
        return safeRead(default = false) {
            val granted = client.permissionController.getGrantedPermissions()
            HealthConnectClientWrapper.PERMISSIONS.all { it in granted }
        }
    }

    override fun getRequiredPermissions(): Set<String> = HealthConnectClientWrapper.PERMISSIONS
    //endregion

    //region Data collection and aggregation
    /**
     * Get today snapshot using multiple aggregates and reads
     *
     * @return
     */
    override suspend fun getTodaySnapshot(): HealthSnapshot {
        val now = Instant.now()
        val startOfDay = LocalDate.now().atStartOfDay(zone).toInstant()
        val yesterday = now.minus(1, ChronoUnit.DAYS)
        val thirtyDaysAgo = now.minus(30, ChronoUnit.DAYS)

        val activityData = getActivityAggregates(startOfDay, now)
        val exercises = getExercisesInRange(startOfDay, now)

        val sleep = getLatestSleep(yesterday, now)

        val heartRate = getLatestHeartRate(yesterday, now)
        getLatestWeight(thirtyDaysAgo, now)

        return HealthSnapshot(
            date = LocalDate.now(),
            steps = activityData.steps,
            distanceMeters = activityData.distance,
            activeCalories = activityData.activeCalories,
            totalCalories = activityData.totalCalories,
            floorsClimbed = activityData.floors,
            exerciseCount = exercises.size,
            exerciseDurationMinutes = exercises.sumOf {
                Duration.between(it.startTime, it.endTime).toMinutes()
            },
            sleepDurationMinutes = sleep?.durationMinutes ?: 0,
            sleepStartTime = sleep?.startTime,
            sleepEndTime = sleep?.endTime,
            heartRate = heartRate ?: 0,
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
        return safeRead(default = emptyMap()) {
            val result = mutableMapOf<LocalDate, Long>()

            val response = client.aggregateGroupByPeriod(
                AggregateGroupByPeriodRequest(
                    metrics = setOf(StepsRecord.COUNT_TOTAL),
                    timeRangeFilter = TimeRangeFilter.between(
                        startTime = startDate.atStartOfDay(zone).toLocalDateTime(),
                        endTime = endDate.plusDays(1).atStartOfDay(zone).toLocalDateTime()
                    ),
                    timeRangeSlicer = Period.ofDays(1)
                )
            )

            response.forEach { bucket ->
                val date = bucket.startTime.atZone(zone).toLocalDate()
                val steps = bucket.result[StepsRecord.COUNT_TOTAL] ?: 0L
                result[date] = steps
            }

            // Fill in missing dates with 0
            var current = startDate
            while (!current.isAfter(endDate)) {
                result.putIfAbsent(current, 0L)
                current = current.plusDays(1)
            }

            result
        }
    }

    override suspend fun getActiveCaloriesForDate(date: LocalDate): Long {
        val startOfDay = date.atStartOfDay(zone).toInstant()
        val endOfDay = date.plusDays(1).atStartOfDay(zone).toInstant()
        return getActiveCaloriesInRange(startOfDay, endOfDay).toLong()
    }

    override suspend fun getActiveCaloriesForDateRange(
        startDate: LocalDate,
        endDate: LocalDate,
    ): Map<LocalDate, Long> {
        TODO("Not yet implemented")
    }

    private suspend fun getActivityAggregates(
        startTime: Instant,
        endTime: Instant,
    ): ActivityAggregates {
        return safeRead(default = ActivityAggregates()) {
            val response = client.aggregate(
                AggregateRequest(
                    metrics = setOf(
                        StepsRecord.COUNT_TOTAL,
                        DistanceRecord.DISTANCE_TOTAL,
                        ActiveCaloriesBurnedRecord.ACTIVE_CALORIES_TOTAL,
                        TotalCaloriesBurnedRecord.ENERGY_TOTAL,
                        FloorsClimbedRecord.FLOORS_CLIMBED_TOTAL,
                    ),
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                )
            )

            ActivityAggregates(
                steps = response[StepsRecord.COUNT_TOTAL] ?: 0L,
                distance = response[DistanceRecord.DISTANCE_TOTAL]?.inMeters ?: 0.0,
                activeCalories = response[ActiveCaloriesBurnedRecord.ACTIVE_CALORIES_TOTAL]
                    ?.inKilocalories ?: 0.0,
                totalCalories = response[TotalCaloriesBurnedRecord.ENERGY_TOTAL]
                    ?.inKilocalories ?: 0.0,
                floors = response[FloorsClimbedRecord.FLOORS_CLIMBED_TOTAL]?.toLong() ?: 0L,
            )
        }
    }
    //endregion

    //region Aggregates
    private suspend fun getStepsInRange(startTime: Instant, endTime: Instant): Long {
        return safeRead(default = 0L) {
            val response = client.aggregate(
                AggregateRequest(
                    metrics = setOf(StepsRecord.COUNT_TOTAL),
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                )
            )
            response[StepsRecord.COUNT_TOTAL] ?: 0L
        }
    }

    private suspend fun getActiveCaloriesInRange(startTime: Instant, endTime: Instant): Double {
        return safeRead(default = 0.0) {
            val response = client.aggregate(
                AggregateRequest(
                    metrics = setOf(ActiveCaloriesBurnedRecord.ACTIVE_CALORIES_TOTAL),
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                )
            )
            response[ActiveCaloriesBurnedRecord.ACTIVE_CALORIES_TOTAL]?.inKilocalories ?: 0.0
        }
    }

    // Records that need individual reads (no aggregate available)
    private suspend fun getExercisesInRange(
        startTime: Instant,
        endTime: Instant,
    ): List<ExerciseSessionRecord> {
        return safeRead(default = emptyList()) {
            val request = ReadRecordsRequest(
                recordType = ExerciseSessionRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
            client.readRecords(request).records
        }
    }
    //endregion

    //region Sleep
    private data class SleepData(
        val durationMinutes: Long,
        val startTime: String,
        val endTime: String,
    )

    private suspend fun getLatestSleep(startTime: Instant, endTime: Instant): SleepData? {
        return safeRead(default = null) {
            val request = ReadRecordsRequest(
                recordType = SleepSessionRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
            val latestSleep = client.readRecords(request).records.maxByOrNull { it.endTime }

            latestSleep?.let {
                val duration = Duration.between(it.startTime, it.endTime).toMinutes()
                val start = LocalTime.ofInstant(it.startTime, zone).format(timeFormatter)
                val end = LocalTime.ofInstant(it.endTime, zone).format(timeFormatter)
                SleepData(duration, start, end)
            }
        }
    }
    //endregion

    //region Heart Rate & Weight
    private suspend fun getLatestHeartRate(startTime: Instant, endTime: Instant): Long? {
        return safeRead(default = null) {
            val request = ReadRecordsRequest(
                recordType = HeartRateRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
            client.readRecords(request).records
                .maxByOrNull { it.endTime }
                ?.samples
                ?.lastOrNull()
                ?.beatsPerMinute
        }
    }

    private suspend fun getLatestWeight(startTime: Instant, endTime: Instant): Double? {
        return safeRead(default = null) {
            val request = ReadRecordsRequest(
                recordType = WeightRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
            client.readRecords(request).records
                .maxByOrNull { it.time }
                ?.weight
                ?.inKilograms
        }
    }
    //endregion
}