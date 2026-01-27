package dev.tomislavmiksik.peak.core.data.local.converters

import androidx.room.TypeConverter
import dev.tomislavmiksik.peak.core.data.local.entity.Gender
import dev.tomislavmiksik.peak.core.data.local.entity.MeasurementType
import dev.tomislavmiksik.peak.core.data.local.entity.PhotoPose
import dev.tomislavmiksik.peak.core.domain.model.LengthUnit
import dev.tomislavmiksik.peak.core.domain.model.WeightUnit
import java.time.Instant
import java.time.LocalDate


//TODO: Split into multiple converter classes if it grows too big
class Converters {

    // LocalDate
    @TypeConverter
    fun fromLocalDate(date: LocalDate?): Long? = date?.toEpochDay()

    @TypeConverter
    fun toLocalDate(epochDay: Long?): LocalDate? = epochDay?.let { LocalDate.ofEpochDay(it) }

    // Instant
    @TypeConverter
    fun fromInstant(instant: Instant?): Long? = instant?.toEpochMilli()

    @TypeConverter
    fun toInstant(epochMilli: Long?): Instant? = epochMilli?.let { Instant.ofEpochMilli(it) }

    // WeightUnit
    @TypeConverter
    fun fromWeightUnit(unit: WeightUnit): String = unit.name

    @TypeConverter
    fun toWeightUnit(name: String): WeightUnit = WeightUnit.valueOf(name)

    // LengthUnit
    @TypeConverter
    fun fromLengthUnit(unit: LengthUnit): String = unit.name

    @TypeConverter
    fun toLengthUnit(name: String): LengthUnit = LengthUnit.valueOf(name)

    // MeasurementType
    @TypeConverter
    fun fromMeasurementType(type: MeasurementType): String = type.name

    @TypeConverter
    fun toMeasurementType(name: String): MeasurementType = MeasurementType.valueOf(name)

    // PhotoPose
    @TypeConverter
    fun fromPhotoPose(pose: PhotoPose): String = pose.name

    @TypeConverter
    fun toPhotoPose(name: String): PhotoPose = PhotoPose.valueOf(name)

    // Gender
    @TypeConverter
    fun fromGender(gender: Gender?): String? = gender?.name

    @TypeConverter
    fun toGender(name: String?): Gender? = name?.let { Gender.valueOf(it) }
}