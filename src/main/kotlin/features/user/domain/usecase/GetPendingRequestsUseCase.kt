package com.haykor.features.user.domain.usecase

import com.haykor.features.user.domain.model.PendingRequestType
import com.haykor.features.user.domain.model.RelationshipStatus
import com.haykor.features.user.domain.model.UserRelationship
import com.haykor.features.user.domain.repository.UserRelationshipRepository

class GetPendingRequestsUseCase(
    private val userRelationshipRepository: UserRelationshipRepository,
) {
    suspend operator fun invoke(userId: Int, type: PendingRequestType?): List<UserRelationship> {
        val pendingRequests = userRelationshipRepository
            .getRelationships(userId)
            .filter { it.status == RelationshipStatus.Pending }
        return when (type) {
            PendingRequestType.Incoming -> pendingRequests.filter { it.addresseeId == userId }
            PendingRequestType.Outgoing -> pendingRequests.filter { it.requesterId == userId }
            null -> pendingRequests
        }
    }
}
