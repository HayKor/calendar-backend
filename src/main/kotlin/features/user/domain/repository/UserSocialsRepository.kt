package com.haykor.features.user.domain.repository

import com.haykor.features.user.domain.model.User

interface UserSocialsRepository {
    suspend fun assignSocialsToUser(
        user: User,
        provider: String,
        externalId: String,
    )
}
