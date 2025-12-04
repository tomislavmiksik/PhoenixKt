package dev.tomislavmiksik.phoenix.core.data.repository

import dev.tomislavmiksik.phoenix.core.data.remote.api.AuthApi
import dev.tomislavmiksik.phoenix.core.data.remote.dto.LoginRequestDto
import dev.tomislavmiksik.phoenix.core.domain.repository.AuthRepository
import javax.inject.Inject

internal class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<String> {
        return try {
            val response = authApi.login(LoginRequestDto(email, password))
            Result.success(response.jwtToken)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            // TODO: Clear stored token/session
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun isAuthenticated(): Boolean {
        // TODO: Check if token exists and is valid
        return false
    }
}
