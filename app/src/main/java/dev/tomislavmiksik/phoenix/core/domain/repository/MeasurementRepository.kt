package dev.tomislavmiksik.phoenix.core.domain.repository

import dev.tomislavmiksik.phoenix.core.domain.model.Measurement
import kotlinx.coroutines.flow.Flow

interface MeasurementRepository {

    fun getAllMeasurements(): Flow<List<Measurement>>

    fun getRecentMeasurements(limit: Int = 10): Flow<List<Measurement>>

    suspend fun getMeasurementById(id: Long): Measurement?

    suspend fun createMeasurement(measurement: Measurement): Result<Measurement>

    suspend fun updateMeasurement(measurement: Measurement): Result<Measurement>

    suspend fun deleteMeasurement(id: Long): Result<Unit>

    suspend fun syncMeasurements(): Result<Unit>
}
