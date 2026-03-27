@file:OptIn(ExperimentalUuidApi::class)

package com.haykor.features.auth.data

import com.haykor.features.user.data.UserTable
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.datetime.datetime
import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

object AuthSessionTable : IntIdTable("auth_sessions") {
    val user = reference("user_id", UserTable)
    val userIp = varchar("user_ip", 255)
    val userAgent = varchar("user_agent", 255)
    val refreshToken = uuid("refresh_token").clientDefault { Uuid.random() }
    val createdAt = datetime("created_at").clientDefault {
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    }
    val updatedAt = datetime("updated_at").clientDefault {
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    }
}