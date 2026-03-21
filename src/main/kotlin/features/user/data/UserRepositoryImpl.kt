package com.haykor.features.user.data

import com.haykor.features.user.domain.CreateUserParams
import com.haykor.features.user.domain.User
import com.haykor.features.user.domain.UserRepository
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.insertReturning
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class UserRepositoryImpl(
    private val database: Database
) : UserRepository {

    override suspend fun create(user: CreateUserParams): User = transaction(database) {
        val id = UserTable.insertAndGetId {
            it[username] = user.name
            it[email] = user.email
            it[hashedPassword] = user.hashedPassword
        }
        UserTable.selectAll().where { UserTable.id eq id.value }
            .map { it.toUser() }
            .single()
    }

    override suspend fun findByEmail(email: String): User? = transaction(database) {
        UserTable.selectAll().where { UserTable.email eq email }
            .map { it.toUser() }
            .singleOrNull()
    }

    override suspend fun findById(id: Int): User? = transaction(database) {
        UserTable.selectAll().where { UserTable.id eq id }
            .map { it.toUser() }
            .singleOrNull()
    }

    private fun ResultRow.toUser() = User(
        id = this[UserTable.id].value,
        name = this[UserTable.username],
        email = this[UserTable.email],
        hashedPassword = this[UserTable.hashedPassword],
        isVerified = this[UserTable.isVerified]
    )
}