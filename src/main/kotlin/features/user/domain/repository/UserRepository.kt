package com.haykor.features.user.domain.repository

import com.haykor.features.user.domain.model.User
import com.haykor.features.user.domain.repository.CreateUserDbParams

interface UserRepository {
    suspend fun create(user: CreateUserDbParams): User

    suspend fun findByEmail(email: String): User?

    suspend fun findBySocials(
        provider: String,
        externalId: String,
    ): User?

    suspend fun findById(id: Int): User?
}
