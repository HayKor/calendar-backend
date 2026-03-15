package com.haykor.features.auth

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(val email: String, val password: String)

@Serializable
data class RefreshTokenRequest(val refreshToken: String)

@Serializable
data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val accessTokenExpiresIn: Long = 30*60, // 30 mins in seconds
    val refreshTokenExpiresIn: Long = 30*24*60*60 // 30 days in seconds
)