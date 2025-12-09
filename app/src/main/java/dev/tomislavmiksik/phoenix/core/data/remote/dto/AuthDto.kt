package dev.tomislavmiksik.phoenix.core.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(
    @SerialName("username")
    val username: String,

    @SerialName("password")
    val password: String
)


@Serializable
data class LoginResponseDto(

    @SerialName("token")
    val jwtToken: String,

    @SerialName("id")
    val userId: Integer,

    @SerialName("username")
    val username: String,

    @SerialName("email")
    val email: String,
)