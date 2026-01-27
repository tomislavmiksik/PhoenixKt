package dev.tomislavmiksik.peak.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.tomislavmiksik.peak.core.domain.model.LengthUnit
import dev.tomislavmiksik.peak.core.domain.model.WeightUnit
import java.time.Instant
import java.time.LocalDate

@Entity(tableName = "user")
data class User(
    @PrimaryKey
    val id: Int = 1,

    val name: String? = null,

    val goalSteps: Int = 10_000,
    val goalCalories: Int = 500,
    val goalActiveMinutes: Int = 30,

    val weightUnit: WeightUnit = WeightUnit.KG,
    val lengthUnit: LengthUnit = LengthUnit.CM,

    val height: Double? = null,
    val birthDate: LocalDate? = null,
    val gender: Gender? = null,

    val createdAt: Instant = Instant.now(),
)


enum class Gender { MALE, FEMALE, OTHER }