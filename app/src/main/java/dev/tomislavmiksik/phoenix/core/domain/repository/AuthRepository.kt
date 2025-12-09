package dev.tomislavmiksik.phoenix.core.domain.repository

import dev.tomislavmiksik.phoenix.core.data.remote.dto.LoginResponseDto
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    suspend fun login(email: String, password: String): LoginResponseDto

    suspend fun logout(): Result<Unit>

    fun getAuthFlowState(): Flow<String?>

    suspend fun isAuthenticated(): Boolean
}
