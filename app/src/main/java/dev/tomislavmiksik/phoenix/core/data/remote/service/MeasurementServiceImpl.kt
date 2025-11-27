package dev.tomislavmiksik.phoenix.core.data.remote.service

import dev.tomislavmiksik.phoenix.core.data.remote.api.MeasurementApi
import dev.tomislavmiksik.phoenix.core.data.remote.dto.MeasurementRequestDto
import dev.tomislavmiksik.phoenix.core.data.remote.dto.MeasurementResponseDto
import javax.inject.Inject

/**
 * Implementation of MeasurementService that wraps the MeasurementApi.
 *
 * This implementation handles error cases and transforms API responses
 * into Result types for safe error handling in upper layers.
 */
internal class MeasurementServiceImpl @Inject constructor(
    private val measurementApi: MeasurementApi
) : MeasurementService {

    override suspend fun getAllMeasurements(): Result<List<MeasurementResponseDto>> =
        runCatching {
            measurementApi.getAllMeasurements()
        }

    override suspend fun getRecentMeasurements(limit: Int): Result<List<MeasurementResponseDto>> =
        runCatching {
            measurementApi.getRecentMeasurements(limit)
        }

    override suspend fun getMeasurementById(id: Long): Result<MeasurementResponseDto> =
        runCatching {
            measurementApi.getMeasurementById(id)
        }

    override suspend fun createMeasurement(
        request: MeasurementRequestDto
    ): Result<MeasurementResponseDto> =
        runCatching {
            measurementApi.createMeasurement(request)
        }

    override suspend fun updateMeasurement(
        id: Long,
        request: MeasurementRequestDto
    ): Result<MeasurementResponseDto> =
        runCatching {
            measurementApi.updateMeasurement(id, request)
        }

    override suspend fun deleteMeasurement(id: Long): Result<Unit> =
        runCatching {
            measurementApi.deleteMeasurement(id)
        }
}
