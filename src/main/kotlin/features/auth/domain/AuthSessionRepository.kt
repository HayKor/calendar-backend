package com.haykor.features.auth.domain

import java.util.UUID

interface AuthSessionRepository {
    suspend fun createSession(session: CreateAuthSession): AuthSession
    suspend fun findAuthSession(refreshTokenUuid: UUID): AuthSession?
}