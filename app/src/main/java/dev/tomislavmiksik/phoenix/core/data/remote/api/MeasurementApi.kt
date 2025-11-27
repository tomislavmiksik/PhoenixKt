package dev.tomislavmiksik.phoenix.core.data.remote.api

import dev.tomislavmiksik.phoenix.core.data.remote.dto.MeasurementRequestDto
import dev.tomislavmiksik.phoenix.core.data.remote.dto.MeasurementResponseDto
import retrofit2.http.*

interface MeasurementApi {

    @GET("api/measurements")
    suspend fun getAllMeasurements(): List<MeasurementResponseDto>

    @GET("api/measurements/recent")
    suspend fun getRecentMeasurements(
        @Query("limit") limit: Int = 10
    ): List<MeasurementResponseDto>

    @GET("api/measurements/{id}")
    suspend fun getMeasurementById(
        @Path("id") id: Long
    ): MeasurementResponseDto

    @POST("api/measurements")
    suspend fun createMeasurement(
        @Body request: MeasurementRequestDto
    ): MeasurementResponseDto

    @PUT("api/measurements/{id}")
    suspend fun updateMeasurement(
        @Path("id") id: Long,
        @Body request: MeasurementRequestDto
    ): MeasurementResponseDto

    @DELETE("api/measurements/{id}")
    suspend fun deleteMeasurement(
        @Path("id") id: Long
    )
}
