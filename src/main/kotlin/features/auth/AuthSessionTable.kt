package com.haykor.features.auth

import com.haykor.features.user.data.UserTable
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object AuthSessionTable : IntIdTable("auth_sessions") {
    val user = reference("user_id", UserTable)
    val userIp = varchar("user_ip", 255)
    val accessToken = varchar("access_token", 50)
    val refreshToken = varchar("refresh_token", 50)
    val createdAt = datetime("created_at").clientDefault {
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    }
}