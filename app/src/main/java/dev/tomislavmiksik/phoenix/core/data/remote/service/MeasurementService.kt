package dev.tomislavmiksik.phoenix.core.data.remote.service

import dev.tomislavmiksik.phoenix.core.data.remote.dto.MeasurementRequestDto
import dev.tomislavmiksik.phoenix.core.data.remote.dto.MeasurementResponseDto

/**
 * Service for handling measurement-related network operations.
 *
 * This service wraps the MeasurementApi and provides error handling,
 * response transformation, and Result-based return types.
 */
interface MeasurementService {

    /**
     * Retrieves all measurements for the current user.
     *
     * @return Result containing a list of MeasurementResponseDto on success,
     *         or an exception on failure.
     */
    suspend fun getAllMeasurements(): Result<List<MeasurementResponseDto>>

    /**
     * Retrieves the most recent measurements for the current user.
     *
     * @param limit The maximum number of measurements to retrieve (default: 10).
     * @return Result containing a list of MeasurementResponseDto on success,
     *         or an exception on failure.
     */
    suspend fun getRecentMeasurements(limit: Int = 10): Result<List<MeasurementResponseDto>>

    /**
     * Retrieves a specific measurement by its ID.
     *
     * @param id The unique identifier of the measurement.
     * @return Result containing the MeasurementResponseDto on success,
     *         or an exception on failure.
     */
    suspend fun getMeasurementById(id: Long): Result<MeasurementResponseDto>

    /**
     * Creates a new measurement.
     *
     * @param request The measurement data to create.
     * @return Result containing the created MeasurementResponseDto on success,
     *         or an exception on failure.
     */
    suspend fun createMeasurement(request: MeasurementRequestDto): Result<MeasurementResponseDto>

    /**
     * Updates an existing measurement.
     *
     * @param id The unique identifier of the measurement to update.
     * @param request The updated measurement data.
     * @return Result containing the updated MeasurementResponseDto on success,
     *         or an exception on failure.
     */
    suspend fun updateMeasurement(
        id: Long,
        request: MeasurementRequestDto
    ): Result<MeasurementResponseDto>

    /**
     * Deletes a measurement by its ID.
     *
     * @param id The unique identifier of the measurement to delete.
     * @return Result containing Unit on success, or an exception on failure.
     */
    suspend fun deleteMeasurement(id: Long): Result<Unit>
}
