package dev.tomislavmiksik.phoenix.core.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "measurements")
data class MeasurementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "user_id")
    val userId: Long,

    @ColumnInfo(name = "weight")
    val weight: Double,

    @ColumnInfo(name = "height")
    val height: Double,

    @ColumnInfo(name = "chest_circumference")
    val chestCircumference: Double? = null,

    @ColumnInfo(name = "arm_circumference")
    val armCircumference: Double? = null,

    @ColumnInfo(name = "leg_circumference")
    val legCircumference: Double? = null,

    @ColumnInfo(name = "waist_circumference")
    val waistCircumference: Double? = null,

    @ColumnInfo(name = "measurement_date")
    val measurementDate: LocalDateTime,

    @ColumnInfo(name = "created_at")
    val createdAt: LocalDateTime,

    @ColumnInfo(name = "updated_at")
    val updatedAt: LocalDateTime
)
