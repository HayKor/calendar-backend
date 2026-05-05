package com.haykor.features.auth.presentation.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val password: String,
)

@Serializable
data class TokenResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("access_token_expires_in") val accessTokenExpiresIn: Long,
    @SerialName("refresh_token_expires_in") val refreshTokenExpiresIn: Long,
)
