package dev.tomislavmiksik.phoenix.core.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MeasurementRequestDto(
    @SerialName("weight")
    val weight: Double,

    @SerialName("height")
    val height: Double,

    @SerialName("chestCircumference")
    val chestCircumference: Double? = null,

    @SerialName("armCircumference")
    val armCircumference: Double? = null,

    @SerialName("legCircumference")
    val legCircumference: Double? = null,

    @SerialName("waistCircumference")
    val waistCircumference: Double? = null,

    @SerialName("measurementDate")
    val measurementDate: String? = null
)

@Serializable
data class MeasurementResponseDto(
    @SerialName("id")
    val id: Long,

    @SerialName("userId")
    val userId: Long,

    @SerialName("weight")
    val weight: Double,

    @SerialName("height")
    val height: Double,

    @SerialName("chestCircumference")
    val chestCircumference: Double? = null,

    @SerialName("armCircumference")
    val armCircumference: Double? = null,

    @SerialName("legCircumference")
    val legCircumference: Double? = null,

    @SerialName("waistCircumference")
    val waistCircumference: Double? = null,

    @SerialName("measurementDate")
    val measurementDate: String,

    @SerialName("createdAt")
    val createdAt: String,

    @SerialName("updatedAt")
    val updatedAt: String
)
