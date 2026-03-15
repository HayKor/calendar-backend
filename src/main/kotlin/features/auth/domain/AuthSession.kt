package com.haykor.features.auth.domain

import java.util.*

data class CreateAuthSession(
    val userId: Int,
    val userIp: String,
    val userAgent: String,
)

data class AuthSession(
    val userId: Int,
    val userIp: String,
    val userAgent: String,
    val accessToken: UUID,
    val refreshToken: UUID
)