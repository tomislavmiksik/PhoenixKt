package dev.tomislavmiksik.peak.core.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import dev.tomislavmiksik.peak.core.domain.model.LengthUnit
import dev.tomislavmiksik.peak.core.domain.model.PercentUnit
import dev.tomislavmiksik.peak.core.domain.model.Unit
import dev.tomislavmiksik.peak.core.domain.model.WeightUnit
import java.time.Instant
import java.time.LocalDate

@Entity(
    tableName = "measurement",
    indices = [
        Index(value = ["date", "type"]),
        Index(value = ["healthConnectId"], unique = true)
    ]
)
data class Measurement(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val date: LocalDate,
    val timestamp: Instant,

    val type: MeasurementType,
    val value: Float,

    // Health Connect sync
    val healthConnectId: String? = null,   // HC record ID (null if not synced yet)
    val sourceApp: String? = null,         // Origin app package name

    val notes: String? = null,
    val createdAt: Instant = Instant.now(),
)

enum class MeasurementType(val unit: Unit) {
    WEIGHT(WeightUnit.KG),
    BODY_FAT(PercentUnit.PERCENT),
    CHEST(LengthUnit.CM),
    WAIST(LengthUnit.CM),
    HIPS(LengthUnit.CM),
    ARMS(LengthUnit.CM),
    THIGHS(LengthUnit.CM),
}