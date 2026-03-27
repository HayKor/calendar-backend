package com.haykor.features.user.data

import com.haykor.features.user.domain.CreateUserParams
import com.haykor.features.user.domain.User
import com.haykor.features.user.domain.UserRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.singleOrNull
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.insertReturning
import org.jetbrains.exposed.v1.r2dbc.select
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction

class UserRepositoryImpl(
    private val database: R2dbcDatabase,
) : UserRepository {
    override suspend fun create(user: CreateUserParams): User = suspendTransaction(database) {
        UserTable
            .insertReturning {
                it[username] = user.name
                it[email] = user.email
                it[hashedPassword] = user.hashedPassword
            }.map { it.toUser() }
            .single()
    }

    override suspend fun findByEmail(email: String): User? = suspendTransaction(database) {
        UserTable
            .selectAll()
            .where { UserTable.email eq email }
            .map { it.toUser() }
            .singleOrNull()
    }

    override suspend fun findById(id: Int): User? = suspendTransaction(database) {
        UserTable
            .selectAll()
            .where { UserTable.id eq id }
            .map { it.toUser() }
            .singleOrNull()
    }

    override suspend fun findBySocials(
        provider: String,
        externalId: String,
    ): User? = suspendTransaction(database) {
        (UserTable innerJoin UserSocialAccountsTable)
            .select(UserTable.columns)
            .where {
                (UserSocialAccountsTable.provider eq provider) and
                    (UserSocialAccountsTable.externalId eq externalId)
            }.map { it.toUser() }
            .singleOrNull()
    }

    private fun ResultRow.toUser() = User(
        id = this[UserTable.id].value,
        name = this[UserTable.username],
        email = this[UserTable.email],
        hashedPassword = this[UserTable.hashedPassword],
        isVerified = this[UserTable.isVerified],
    )
}
