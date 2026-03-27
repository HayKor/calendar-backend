@file:OptIn(ExperimentalUuidApi::class)

package com.haykor.features.auth.domain

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface AuthSessionRepository {
    suspend fun createSession(params: CreateAuthSessionParams): AuthSession

    suspend fun findAuthSession(refreshToken: Uuid): AuthSession?

    suspend fun updateAuthSession(
        refreshToken: Uuid,
        params: UpdateSessionParams,
    ): AuthSession?
}
