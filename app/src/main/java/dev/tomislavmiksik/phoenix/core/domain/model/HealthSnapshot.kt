package dev.tomislavmiksik.phoenix.core.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity
data class HealthSnapshot(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val date: LocalDate = LocalDate.now(),

    // Activity
    val steps: Long = 0,
    val distanceMeters: Double = 0.0,
    val activeCalories: Double = 0.0,
    val totalCalories: Double = 0.0,
    val floorsClimbed: Long = 0,
    val exerciseCount: Int = 0,
    val exerciseDurationMinutes: Long = 0,

    // Sleep
    val sleepDurationMinutes: Long = 0,
    val sleepStartTime: String? = null,
    val sleepEndTime: String? = null,

    // Vitals
    val heartRate: Long = 0,
    val weight: Double = 0.0
)
