package dev.tomislavmiksik.phoenix.core.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity
data class HealthSnapshot(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val steps: Long,
    val sleepDuration: LocalDateTime?,
    val heartRate: Long,
    val weight: Double,
    val exerciseCount: Int
)
