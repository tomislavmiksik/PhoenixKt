package dev.tomislavmiksik.peak.core.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import dev.tomislavmiksik.peak.core.data.local.entity.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM user WHERE id = 1")
    fun getUser(): Flow<User?>

    @Query("SELECT * FROM user WHERE id = 1")
    suspend fun getUserSync(): User?

    @Upsert
    suspend fun upsert(user: User)
}