package com.haykor.features.user.domain

interface UserSocialsRepository {
    suspend fun assignSocialsToUser(
        user: User,
        provider: String,
        externalId: String,
    )
}
