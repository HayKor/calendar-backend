@file:OptIn(ExperimentalUuidApi::class)

package com.haykor.features.auth.domain

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class CreateAuthSession(
    val userId: Int,
    val userIp: String,
    val userAgent: String,
)

data class AuthSession(
    val userId: Int,
    val userIp: String,
    val userAgent: String,
    val accessToken: Uuid,
    val refreshToken: Uuid
)