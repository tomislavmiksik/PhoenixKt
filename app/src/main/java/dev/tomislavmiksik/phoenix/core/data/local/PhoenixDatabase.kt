package dev.tomislavmiksik.phoenix.core.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.tomislavmiksik.phoenix.core.data.local.converters.LocalDateTimeConverter
import dev.tomislavmiksik.phoenix.core.data.local.dao.ExerciseDao
import dev.tomislavmiksik.phoenix.core.data.local.dao.MeasurementDao
import dev.tomislavmiksik.phoenix.core.data.local.dao.ProgressPointDao
import dev.tomislavmiksik.phoenix.core.data.local.entity.ExerciseEntity
import dev.tomislavmiksik.phoenix.core.data.local.entity.MeasurementEntity
import dev.tomislavmiksik.phoenix.core.data.local.entity.ProgressPoint

@Database(
    entities = [
        ExerciseEntity::class,
        ProgressPoint::class,
        MeasurementEntity::class,
    ],
    version = DatabaseMigrations.DB_VERSION,
    exportSchema = false
)
@TypeConverters(
    LocalDateTimeConverter::class,

)
abstract class PhoenixDatabase : RoomDatabase() {
    abstract fun getExerciseDao(): ExerciseDao
    abstract fun getProgressPointDao(): ProgressPointDao
    abstract fun getMeasurementDao(): MeasurementDao

    companion object {
        private const val DB_NAME = "phoenix_database"

        @Volatile
        @Suppress("ktlint:standard:property-naming")
        private var INSTANCE: PhoenixDatabase? = null

        fun getInstance(context: Context): PhoenixDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(this) {
                val instance =
                    Room.databaseBuilder(
                        context.applicationContext,
                        PhoenixDatabase::class.java,
                        DB_NAME,
                    ).addMigrations(*DatabaseMigrations.MIGRATIONS).build()

                INSTANCE = instance
                return instance
            }
        }
    }
}
