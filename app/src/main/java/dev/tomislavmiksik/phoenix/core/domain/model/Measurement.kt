package dev.tomislavmiksik.phoenix.core.domain.model

import java.time.LocalDateTime

data class Measurement(
    val id: Long = 0,
    val userId: Long,
    val weight: Double,
    val height: Double,
    val chestCircumference: Double? = null,
    val armCircumference: Double? = null,
    val legCircumference: Double? = null,
    val waistCircumference: Double? = null,
    val measurementDate: LocalDateTime,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
