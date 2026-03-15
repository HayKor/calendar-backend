package com.haykor.features.auth.data

import com.haykor.features.user.data.UserTable
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import java.util.UUID

object AuthSessionTable : IntIdTable("auth_sessions") {
    val user = reference("user_id", UserTable)
    val userIp = varchar("user_ip", 255)
    val userAgent = varchar("user_agent", 255)
    val accessToken = uuid("access_token").clientDefault { UUID.randomUUID() }
    val refreshToken = uuid("refresh_token").clientDefault { UUID.randomUUID() }
    val createdAt = datetime("created_at").clientDefault {
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    }
}