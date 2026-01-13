package dev.tomislavmiksik.phoenix.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.tomislavmiksik.phoenix.core.data.local.converters.LocalDateTimeConverter
import dev.tomislavmiksik.phoenix.core.domain.model.HealthSnapshot

@Database(
    entities = [HealthSnapshot::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    LocalDateTimeConverter::class,
)
abstract class PhoenixDatabase : RoomDatabase() {
    // TODO: Add DAOs here when implementing local caching for health data
}
