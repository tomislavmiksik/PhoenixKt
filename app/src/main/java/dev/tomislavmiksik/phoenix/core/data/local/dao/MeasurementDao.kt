package dev.tomislavmiksik.phoenix.core.data.local.dao

import androidx.room.*
import dev.tomislavmiksik.phoenix.core.data.local.entity.MeasurementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MeasurementDao {

    @Query("SELECT * FROM measurements WHERE user_id = :userId ORDER BY measurement_date DESC")
    fun getAllMeasurements(userId: Long): Flow<List<MeasurementEntity>>

    @Query("SELECT * FROM measurements WHERE user_id = :userId ORDER BY measurement_date DESC LIMIT :limit")
    fun getRecentMeasurements(userId: Long, limit: Int = 10): Flow<List<MeasurementEntity>>

    @Query("SELECT * FROM measurements WHERE id = :id")
    suspend fun getMeasurementById(id: Long): MeasurementEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeasurement(measurement: MeasurementEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeasurements(measurements: List<MeasurementEntity>)

    @Update
    suspend fun updateMeasurement(measurement: MeasurementEntity)

    @Delete
    suspend fun deleteMeasurement(measurement: MeasurementEntity)

    @Query("DELETE FROM measurements WHERE id = :id")
    suspend fun deleteMeasurementById(id: Long)

    @Query("DELETE FROM measurements WHERE user_id = :userId")
    suspend fun deleteAllMeasurementsForUser(userId: Long)
}
