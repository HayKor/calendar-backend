package com.haykor.plugins

import com.haykor.features.auth.AuthSessionTable
import com.haykor.features.user.data.UserTable
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureDatabases() {
    val database = Database.connect(
        "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;",
        driver = "org.h2.Driver"
    )
    transaction(database) {
        SchemaUtils.create(UserTable, AuthSessionTable)
    }
}