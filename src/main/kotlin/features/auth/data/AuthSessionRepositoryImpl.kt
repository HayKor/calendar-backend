@file:OptIn(ExperimentalUuidApi::class)

package com.haykor.features.auth.data

import com.haykor.features.auth.domain.AuthSession
import com.haykor.features.auth.domain.AuthSessionRepository
import com.haykor.features.auth.domain.CreateAuthSession
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.singleOrNull
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.insertReturning
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class AuthSessionRepositoryImpl(
    private val database: R2dbcDatabase
) : AuthSessionRepository {

    override suspend fun createSession(session: CreateAuthSession): AuthSession = suspendTransaction(database) {
        AuthSessionTable.insertReturning {
            it[this.user] = session.userId
            it[this.userIp] = session.userIp
            it[this.userAgent] = session.userAgent
        }.map { it.toAuthSession() }.single()
    }

    override suspend fun findAuthSession(refreshTokenUuid: Uuid): AuthSession? = suspendTransaction(database) {
        AuthSessionTable.selectAll().where { AuthSessionTable.refreshToken eq refreshTokenUuid }
            .map { it.toAuthSession() }
            .singleOrNull()
    }

    private fun ResultRow.toAuthSession(): AuthSession = AuthSession(
        userId = this[AuthSessionTable.user].value,
        userIp = this[AuthSessionTable.userIp],
        userAgent = this[AuthSessionTable.userAgent],
        accessToken = this[AuthSessionTable.accessToken],
        refreshToken = this[AuthSessionTable.refreshToken]
    )
}