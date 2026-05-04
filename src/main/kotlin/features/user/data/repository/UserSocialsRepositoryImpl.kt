package com.haykor.features.user.data.repository

import com.haykor.features.user.data.local.UserSocialAccountsTable
import com.haykor.features.user.domain.model.User
import com.haykor.features.user.domain.repository.UserSocialsRepository
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.insert
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction

class UserSocialsRepositoryImpl(
    private val database: R2dbcDatabase,
) : UserSocialsRepository {
    override suspend fun assignSocialsToUser(
        user: User,
        provider: String,
        externalId: String,
    ): Unit = suspendTransaction(database) {
        UserSocialAccountsTable.insert {
            it[this.user] = user.id
            it[this.provider] = provider
            it[this.externalId] = externalId
        }
    }
}
