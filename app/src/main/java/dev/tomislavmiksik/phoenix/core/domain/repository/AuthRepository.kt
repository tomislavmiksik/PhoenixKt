package dev.tomislavmiksik.phoenix.core.domain.repository

interface AuthRepository {

  suspend fun login(email: String, password: String): Result<String>

  suspend fun logout(): Result<Unit>

  suspend fun isAuthenticated(): Boolean
}
