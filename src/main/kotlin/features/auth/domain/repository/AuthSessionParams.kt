@file:OptIn(ExperimentalUuidApi::class)

package com.haykor.features.auth.domain.repository

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class CreateAuthSessionParams(
    val userId: Int,
    val userIp: String,
    val userAgent: String,
)

data class UpdateSessionParams(
    val refreshToken: Uuid,
    val userIp: String,
    val userAgent: String,
)
