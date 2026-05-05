@file:OptIn(ExperimentalUuidApi::class)

package com.haykor.features.auth.domain.model

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class AuthSession(
    val userId: Int,
    val userIp: String,
    val userAgent: String,
    val refreshToken: Uuid,
)
