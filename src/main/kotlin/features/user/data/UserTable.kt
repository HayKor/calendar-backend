package com.haykor.features.user.data

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.datetime.datetime
import kotlin.time.Clock

object UserTable : IntIdTable("users") {
    val username = varchar("username", 50).uniqueIndex()
    val email = varchar("email", 100).uniqueIndex()
    val hashedPassword = varchar("hashed_password", 255).nullable()
    val isVerified = bool("isVerified").nullable()
    val createdAt = datetime("created_at").clientDefault {
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    }
}