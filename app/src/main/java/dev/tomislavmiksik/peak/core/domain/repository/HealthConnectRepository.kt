package dev.tomislavmiksik.peak.core.domain.repository

import dev.tomislavmiksik.peak.core.data.local.entity.HealthSnapshot
import java.time.LocalDate

interface HealthConnectRepository {
    suspend fun isAvailable(): Boolean
    suspend fun hasAllPermissions(): Boolean
    fun getRequiredPermissions(): Set<String>

    suspend fun getTodaySnapshot(): HealthSnapshot
    suspend fun getStepsForDate(date: LocalDate): Long
    suspend fun getStepsForDateRange(startDate: LocalDate, endDate: LocalDate): Map<LocalDate, Long>
    suspend fun getActiveCaloriesForDate(date: LocalDate): Long
    suspend fun getActiveCaloriesForDateRange(
        startDate: LocalDate,
        endDate: LocalDate,
    ): Map<LocalDate, Long>
}
