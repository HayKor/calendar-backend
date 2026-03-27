@file:OptIn(ExperimentalUuidApi::class)

package com.haykor.features.auth.domain

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class Auth(
    val refreshToken: Uuid,
    val accessToken: String,
    val refreshTokenExpiresIn: Long,
    val accessTokenExpiresIn: Long,
)
