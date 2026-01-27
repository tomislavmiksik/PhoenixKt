package dev.tomislavmiksik.peak.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.tomislavmiksik.peak.core.data.local.converters.Converters
import dev.tomislavmiksik.peak.core.data.local.dao.HealthSnapshotDao
import dev.tomislavmiksik.peak.core.data.local.dao.MeasurementDao
import dev.tomislavmiksik.peak.core.data.local.dao.ProgressPhotoDao
import dev.tomislavmiksik.peak.core.data.local.dao.UserDao
import dev.tomislavmiksik.peak.core.data.local.entity.HealthSnapshot
import dev.tomislavmiksik.peak.core.data.local.entity.Measurement
import dev.tomislavmiksik.peak.core.data.local.entity.ProgressPhoto
import dev.tomislavmiksik.peak.core.data.local.entity.User

@Database(
    entities = [
        HealthSnapshot::class,
        User::class,
        ProgressPhoto::class,
        Measurement::class,
    ],
    version = 3,
    exportSchema = true
)
@TypeConverters(
    Converters::class,
)
abstract class PeakDatabase : RoomDatabase() {
    abstract fun healthSnapshotDao(): HealthSnapshotDao
    abstract fun userDao(): UserDao
    abstract fun measurementDao(): MeasurementDao
    abstract fun progressPhotoDao(): ProgressPhotoDao

}
