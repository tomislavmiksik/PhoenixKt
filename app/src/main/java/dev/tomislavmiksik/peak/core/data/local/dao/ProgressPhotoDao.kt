// core/data/local/dao/ProgressPhotoDao.kt

package dev.tomislavmiksik.peak.core.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import dev.tomislavmiksik.peak.core.data.local.entity.ProgressPhoto
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface ProgressPhotoDao {

    @Query("SELECT * FROM progress_photo ORDER BY date DESC")
    fun getAll(): Flow<List<ProgressPhoto>>

    @Query("SELECT * FROM progress_photo ORDER BY date DESC")
    suspend fun getAllSync(): List<ProgressPhoto>

    @Query("SELECT * FROM progress_photo ORDER BY date ASC LIMIT 1")
    fun getFirst(): Flow<ProgressPhoto?>

    @Query("SELECT * FROM progress_photo ORDER BY date ASC LIMIT 1")
    suspend fun getFirstSync(): ProgressPhoto?

    @Query("SELECT * FROM progress_photo ORDER BY date DESC LIMIT 1")
    fun getLatest(): Flow<ProgressPhoto?>

    @Query("SELECT * FROM progress_photo ORDER BY date DESC LIMIT 1")
    suspend fun getLatestSync(): ProgressPhoto?

    @Query("SELECT * FROM progress_photo WHERE id = :id")
    fun getById(id: Long): Flow<ProgressPhoto?>

    @Query("SELECT * FROM progress_photo WHERE date = :date")
    suspend fun getByDate(date: LocalDate): ProgressPhoto?

    @Query("SELECT * FROM progress_photo WHERE filename = :filename LIMIT 1")
    suspend fun getByFilename(filename: String): ProgressPhoto?

    @Query("SELECT COUNT(*) FROM progress_photo")
    fun getCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM progress_photo")
    suspend fun getCountSync(): Int

    @Insert
    suspend fun insert(photo: ProgressPhoto): Long

    @Update
    suspend fun update(photo: ProgressPhoto)

    @Delete
    suspend fun delete(photo: ProgressPhoto)
}