package com.haykor.features.auth.data

import com.haykor.features.auth.domain.AuthSession
import com.haykor.features.auth.domain.AuthSessionRepository
import com.haykor.features.auth.domain.CreateAuthSession
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class AuthSessionRepositoryImpl(
    private val database: Database
) : AuthSessionRepository {

    override suspend fun createSession(session: CreateAuthSession): AuthSession = transaction(database) {
        AuthSessionTable.insert {
            it[this.user] = session.userId
            it[this.userIp] = session.userIp
            it[this.userAgent] = session.userAgent
        }.resultedValues!!.first().toAuthSession()
    }

    override suspend fun findAuthSession(refreshTokenUuid: UUID): AuthSession? = transaction(database) {
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