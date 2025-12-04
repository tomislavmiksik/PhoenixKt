package dev.tomislavmiksik.phoenix.core.data.remote.api

import dev.tomislavmiksik.phoenix.core.data.remote.dto.MeasurementRequestDto
import dev.tomislavmiksik.phoenix.core.data.remote.dto.MeasurementResponseDto
import retrofit2.http.*

interface MeasurementApi {

    @GET("/measurements")
    suspend fun getAllMeasurements(): List<MeasurementResponseDto>

    @GET("/measurements/recent")
    suspend fun getRecentMeasurements(
        @Query("limit") limit: Int = 10
    ): List<MeasurementResponseDto>

    @GET("/measurements/{id}")
    suspend fun getMeasurementById(
        @Path("id") id: Long
    ): MeasurementResponseDto

    @POST("/measurements")
    suspend fun createMeasurement(
        @Body request: MeasurementRequestDto
    ): MeasurementResponseDto

    @PUT("/measurements/{id}")
    suspend fun updateMeasurement(
        @Path("id") id: Long,
        @Body request: MeasurementRequestDto
    ): MeasurementResponseDto

    @DELETE("/measurements/{id}")
    suspend fun deleteMeasurement(
        @Path("id") id: Long
    )
}
