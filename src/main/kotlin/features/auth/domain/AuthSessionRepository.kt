@file:OptIn(ExperimentalUuidApi::class)

package com.haykor.features.auth.domain

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface AuthSessionRepository {
    suspend fun createSession(session: CreateAuthSession): AuthSession
    suspend fun findAuthSession(refreshTokenUuid: Uuid): AuthSession?
}