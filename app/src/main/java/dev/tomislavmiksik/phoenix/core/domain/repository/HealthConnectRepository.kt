package dev.tomislavmiksik.phoenix.core.domain.repository

import dev.tomislavmiksik.phoenix.core.domain.model.HealthSnapshot
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
