package dev.tomislavmiksik.phoenix.core.data.local.converters

import androidx.room.TypeConverter
import java.time.LocalDate

class LocalDateTimeConverter {

    @TypeConverter
    fun localDateToString(value: LocalDate?): String? {
        return value?.toString()
    }

    @TypeConverter
    fun stringToLocalDate(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(it) }
    }
}
