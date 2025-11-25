package dev.tomislavmiksik.phoenix.core.data.local.converters

import androidx.room.TypeConverter
import java.util.Date

class DateConverter{

    @TypeConverter
    fun timestampToDate(value : Long?) : Date? {
        return if (value != null)  Date(value) else null;
    }

    @TypeConverter
    fun dateToTimestamp(value: Date?) : Long? {
        return if (value != null)  value.time else null;
    }
}