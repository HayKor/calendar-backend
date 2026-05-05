package com.haykor.features.auth.domain.repository

import com.haykor.features.auth.domain.model.AuthSession
import com.haykor.features.auth.domain.model.CreateAuthSessionParams
import com.haykor.features.auth.domain.model.UpdateSessionParams
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface AuthSessionRepository {
    suspend fun createSession(params: CreateAuthSessionParams): AuthSession

    @OptIn(ExperimentalUuidApi::class)
    suspend fun findAuthSession(refreshToken: Uuid): AuthSession?

    @OptIn(ExperimentalUuidApi::class)
    suspend fun updateAuthSession(
        refreshToken: Uuid,
        params: UpdateSessionParams,
    ): AuthSession?
}