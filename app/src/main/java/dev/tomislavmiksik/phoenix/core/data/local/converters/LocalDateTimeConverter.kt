package dev.tomislavmiksik.phoenix.core.data.local.converters

import androidx.room.TypeConverter
import dev.tomislavmiksik.phoenix.core.util.extensions.toMillis
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Date

class LocalDateTimeConverter{

    @TypeConverter
    fun timestampToLocalDateTime(value : Long?) : LocalDateTime? {
        return if (value != null) LocalDateTime.from(Instant.ofEpochMilli(value)) else null;
    }

    @TypeConverter
    fun dateToTimestamp(value: LocalDateTime?) : Long? {
        return value?.toMillis();
    }
}
