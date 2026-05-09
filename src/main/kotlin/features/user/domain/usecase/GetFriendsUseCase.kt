package com.haykor.features.user.domain.usecase

import com.haykor.features.user.domain.model.User
import com.haykor.features.user.domain.repository.UserRelationshipRepository
import com.haykor.features.user.domain.repository.UserRepository

class GetFriendsUseCase(
    private val userRelationshipRepository: UserRelationshipRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(userId: Int): List<User> {
        val relationships = userRelationshipRepository.getRelationships(userId)
    }
}
