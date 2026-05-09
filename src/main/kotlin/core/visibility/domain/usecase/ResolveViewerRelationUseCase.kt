package com.haykor.core.visibility.domain.usecase

import com.haykor.core.visibility.domain.model.ViewerRelation
import com.haykor.features.user.domain.model.RelationshipStatus
import com.haykor.features.user.domain.repository.UserRelationshipRepository

class ResolveViewerRelationUseCase(
    private val userRelationshipRepository: UserRelationshipRepository,
) {
    suspend operator fun invoke(requesterId: Int, targetUserId: Int): ViewerRelation {
        if (requesterId == targetUserId) return ViewerRelation.Owner

        val relationship = userRelationshipRepository.getRelationshipBetween(requesterId, targetUserId)
        return when (relationship?.status) {
            RelationshipStatus.Accepted -> ViewerRelation.Friend
            else -> ViewerRelation.Stranger
        }
    }
}
