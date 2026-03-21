package com.haykor.features.user.data

import org.jetbrains.exposed.dao.id.IntIdTable

object UserSocialAccountsTable : IntIdTable("user_social_accounts") {
    val user = reference("user_id", UserTable)
    val provider = varchar("provider", 50) // "google", "yandex", etc.
    val externalId = varchar("external_id", 255) // The ID from Google/Yandex

    init {
        uniqueIndex(provider, externalId)
    }
}