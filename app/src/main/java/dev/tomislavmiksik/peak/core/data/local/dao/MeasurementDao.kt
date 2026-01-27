package dev.tomislavmiksik.peak.core.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import dev.tomislavmiksik.peak.core.data.local.entity.Measurement
import dev.tomislavmiksik.peak.core.data.local.entity.MeasurementType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface MeasurementDao {

    // Queries by type
    @Query("SELECT * FROM measurement WHERE type = :type ORDER BY timestamp DESC")
    fun getByType(type: MeasurementType): Flow<List<Measurement>>

    @Query("SELECT * FROM measurement WHERE type = :type ORDER BY timestamp DESC")
    suspend fun getByTypeSync(type: MeasurementType): List<Measurement>

    @Query("SELECT * FROM measurement WHERE type = :type ORDER BY timestamp DESC LIMIT 1")
    fun getLatest(type: MeasurementType): Flow<Measurement?>

    @Query("SELECT * FROM measurement WHERE type = :type ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestSync(type: MeasurementType): Measurement?

    @Query("SELECT * FROM measurement WHERE type = :type ORDER BY timestamp ASC LIMIT 1")
    fun getFirst(type: MeasurementType): Flow<Measurement?>

    @Query("SELECT * FROM measurement WHERE type = :type ORDER BY timestamp ASC LIMIT 1")
    suspend fun getFirstSync(type: MeasurementType): Measurement?

    // Queries by date
    @Query("SELECT * FROM measurement WHERE date = :date")
    fun getByDate(date: LocalDate): Flow<List<Measurement>>

    @Query("SELECT * FROM measurement WHERE date = :date AND type = :type")
    suspend fun getByDateAndType(date: LocalDate, type: MeasurementType): Measurement?

    // Health Connect
    @Query("SELECT * FROM measurement WHERE healthConnectId = :hcId LIMIT 1")
    suspend fun getByHealthConnectId(hcId: String): Measurement?

    @Query("UPDATE measurement SET healthConnectId = :hcId WHERE id = :id")
    suspend fun updateHealthConnectId(id: Long, hcId: String)

    // All measurements (for export)
    @Query("SELECT * FROM measurement ORDER BY timestamp DESC")
    suspend fun getAllSync(): List<Measurement>

    // CRUD
    @Insert
    suspend fun insert(measurement: Measurement): Long

    @Update
    suspend fun update(measurement: Measurement)

    @Delete
    suspend fun delete(measurement: Measurement)
}