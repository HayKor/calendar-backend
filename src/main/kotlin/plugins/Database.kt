package com.haykor.plugins

import com.haykor.features.auth.data.AuthSessionTable
import com.haykor.features.user.data.UserSocialAccountsTable
import com.haykor.features.user.data.UserTable
import io.ktor.server.application.*
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.SchemaUtils
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction

fun Application.configureDatabases() {
    val database = R2dbcDatabase.connect(
        url = environment.config.property("db.url").getString(),
        user = environment.config.property("db.user").getString(),
        password = environment.config.property("db.password").getString()
    )
    runBlocking {
        suspendTransaction(database) {
            SchemaUtils.create(UserTable, UserSocialAccountsTable, AuthSessionTable)
        }
    }
}