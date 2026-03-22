package com.haykor.features.auth.domain

data class Auth(
    val refreshToken: String,
    val accessToken: String,
    val refreshTokenExpiresIn: Long,
    val accessTokenExpiresIn: Long
)
