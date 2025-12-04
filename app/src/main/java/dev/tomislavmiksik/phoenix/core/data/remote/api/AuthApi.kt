package dev.tomislavmiksik.phoenix.core.data.remote.api

import dev.tomislavmiksik.phoenix.core.data.remote.dto.LoginRequestDto
import dev.tomislavmiksik.phoenix.core.data.remote.dto.LoginResponseDto
import retrofit2.http.Body
import retrofit2.http.POST


interface AuthApi {

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequestDto
    ): LoginResponseDto
}