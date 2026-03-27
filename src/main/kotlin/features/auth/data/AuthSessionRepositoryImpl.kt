@file:OptIn(ExperimentalUuidApi::class)

package com.haykor.features.auth.data

import com.haykor.features.auth.domain.AuthSession
import com.haykor.features.auth.domain.AuthSessionRepository
import com.haykor.features.auth.domain.CreateAuthSessionParams
import com.haykor.features.auth.domain.UpdateSessionParams
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.insertReturning
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.r2dbc.updateReturning
import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class AuthSessionRepositoryImpl(
    private val database: R2dbcDatabase,
) : AuthSessionRepository {
    override suspend fun createSession(params: CreateAuthSessionParams): AuthSession = suspendTransaction(database) {
        AuthSessionTable
            .insertReturning {
                it[this.user] = params.userId
                it[this.userIp] = params.userIp
                it[this.userAgent] = params.userAgent
            }.map { it.toAuthSession() }
            .single()
    }

    override suspend fun findAuthSession(refreshToken: Uuid): AuthSession? = suspendTransaction(database) {
        AuthSessionTable
            .selectAll()
            .where { AuthSessionTable.refreshToken eq refreshToken }
            .map { it.toAuthSession() }
            .singleOrNull()
    }

    override suspend fun updateAuthSession(
        refreshToken: Uuid,
        params: UpdateSessionParams,
    ): AuthSession? = suspendTransaction(database) {
        AuthSessionTable
            .updateReturning(where = { AuthSessionTable.refreshToken eq refreshToken }) {
                it[this.refreshToken] = params.refreshToken
                it[this.userIp] = params.userIp
                it[this.userAgent] = params.userAgent

                it[this.updatedAt] = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            }.map { it.toAuthSession() }
            .singleOrNull()
    }

    private fun ResultRow.toAuthSession(): AuthSession = AuthSession(
        userId = this[AuthSessionTable.user].value,
        userIp = this[AuthSessionTable.userIp],
        userAgent = this[AuthSessionTable.userAgent],
        refreshToken = this[AuthSessionTable.refreshToken],
    )
}
