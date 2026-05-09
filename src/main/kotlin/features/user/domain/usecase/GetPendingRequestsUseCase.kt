package com.haykor.features.user.domain.usecase

import com.haykor.features.user.domain.model.UserRelationship
import com.haykor.features.user.domain.repository.UserRelationshipRepository

class GetPendingRequestsUseCase(
    private val userRelationshipRepository: UserRelationshipRepository,
) {
    suspend operator fun invoke(userId: Int): List<UserRelationship> = userRelationshipRepository.getPendingRequests(userId)
}
