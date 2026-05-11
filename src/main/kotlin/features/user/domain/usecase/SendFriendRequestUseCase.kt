package com.haykor.features.user.domain.usecase

import com.haykor.features.user.domain.model.RelationshipStatus
import com.haykor.features.user.domain.model.UserRelationship
import com.haykor.features.user.domain.model.UserRelationshipException
import com.haykor.features.user.domain.repository.UserRelationshipRepository

class SendFriendRequestUseCase(
    private val userRelationshipRepository: UserRelationshipRepository,
) {
    suspend operator fun invoke(requesterId: Int, addresseeId: Int): UserRelationship {
        if (requesterId == addresseeId) throw UserRelationshipException.RequesterEqualsAddressee()

        userRelationshipRepository.getRelationshipBetween(requesterId, addresseeId)?.let { existing ->
            when (existing.status) {
                RelationshipStatus.Pending -> throw UserRelationshipException.PendingRequestAlreadyExists()
                RelationshipStatus.Accepted -> throw UserRelationshipException.AlreadyFriends()
                RelationshipStatus.Blocked -> throw UserRelationshipException.Blocked()
            }
        }

        return userRelationshipRepository.sendRequest(requesterId, addresseeId)
    }
}
