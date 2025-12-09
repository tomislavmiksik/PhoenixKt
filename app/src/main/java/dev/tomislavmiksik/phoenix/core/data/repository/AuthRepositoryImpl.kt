package dev.tomislavmiksik.phoenix.core.data.repository

import dev.tomislavmiksik.phoenix.core.data.datastore.PhoenixPreferencesDataSource
import dev.tomislavmiksik.phoenix.core.data.remote.api.AuthApi
import dev.tomislavmiksik.phoenix.core.data.remote.dto.LoginRequestDto
import dev.tomislavmiksik.phoenix.core.data.remote.dto.LoginResponseDto
import dev.tomislavmiksik.phoenix.core.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val dataSource: PhoenixPreferencesDataSource
) : AuthRepository {

    override suspend fun login(email: String, password: String): LoginResponseDto {
        val response = authApi.login(LoginRequestDto(email, password))

        dataSource.saveJwtToken(response.jwtToken)

        return response
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            dataSource.clearJwtToken()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getAuthFlowState(): Flow<String?> {
        return dataSource.getJwtTokenFlow()
    }


    override suspend fun isAuthenticated(): Boolean {
        return dataSource.getJwtToken() != null
    }
}
