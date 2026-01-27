package dev.tomislavmiksik.peak.core.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant
import java.time.LocalDate

@Entity(
    tableName = "progress_photo",
    indices = [Index("date")]
)
data class ProgressPhoto(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val date: LocalDate,
    val filename: String,

    // Snapshot values at time of photo (always in kg / %)
    val weightKg: Float? = null,
    val bodyFatPercent: Float? = null,

    val pose: PhotoPose = PhotoPose.FRONT,
    val notes: String? = null,

    val createdAt: Instant = Instant.now(),
)

enum class PhotoPose {
    FRONT,
    BACK,
    LEFT_SIDE,
    RIGHT_SIDE
}