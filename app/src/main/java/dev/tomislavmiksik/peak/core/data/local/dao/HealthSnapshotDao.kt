package dev.tomislavmiksik.peak.core.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import dev.tomislavmiksik.peak.core.data.local.entity.HealthSnapshot
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface HealthSnapshotDao {

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insert(healthSnapshot: HealthSnapshot): Long

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertAll(healthSnapshots: List<HealthSnapshot>)

    @Update
    suspend fun update(healthSnapshot: HealthSnapshot)

    @Delete
    suspend fun delete(healthSnapshot: HealthSnapshot)

    @Query("SELECT * FROM HealthSnapshot ORDER BY date DESC")
    fun getAllSnapshots(): Flow<List<HealthSnapshot>>

    @Query("SELECT * FROM HealthSnapshot ORDER BY date DESC LIMIT 1")
    suspend fun getLatestSnapshot(): HealthSnapshot?

    @Query("SELECT * FROM HealthSnapshot ORDER BY date DESC LIMIT 1")
    fun getLatestSnapshotFlow(): Flow<HealthSnapshot?>

    @Query("SELECT * FROM HealthSnapshot WHERE id = :id")
    suspend fun getSnapshotById(id: Long): HealthSnapshot?

    @Query("SELECT * FROM HealthSnapshot WHERE date = :date")
    suspend fun getSnapshotByDate(date: LocalDate): HealthSnapshot?

    @Query("SELECT * FROM HealthSnapshot WHERE date >= :startDate AND date <= :endDate ORDER BY date DESC")
    suspend fun getSnapshotsInDateRange(
        startDate: LocalDate,
        endDate: LocalDate,
    ): List<HealthSnapshot>

    @Query("SELECT * FROM HealthSnapshot WHERE date >= :startDate AND date <= :endDate ORDER BY date DESC")
    fun getSnapshotsInDateRangeFlow(
        startDate: LocalDate,
        endDate: LocalDate,
    ): Flow<List<HealthSnapshot>>

    @Query(
        "DELETE FROM HealthSnapshot WHERE id NOT IN " +
                "(SELECT id FROM HealthSnapshot ORDER BY date DESC LIMIT :keepCount)"
    )
    suspend fun deleteOldSnapshots(keepCount: Int)

    @Query("DELETE FROM HealthSnapshot")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM HealthSnapshot")
    suspend fun getCount(): Int
}