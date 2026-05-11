package com.haykor.features.user.domain.usecase

import com.haykor.features.user.domain.model.RelationshipStatus
import com.haykor.features.user.domain.model.UserRelationship
import com.haykor.features.user.domain.model.UserRelationshipException
import com.haykor.features.user.domain.repository.UserRelationshipRepository

/**
 * Update relationship use case (Accept/Decline it)
 *
 * @property userRelationshipRepository
 * @constructor Create empty Update relationship use case
 */
class UpdateRelationshipUseCase(
    private val userRelationshipRepository: UserRelationshipRepository,
) {
    suspend operator fun invoke(userId: Int, id: Int, status: RelationshipStatus): UserRelationship {
        val relationship = userRelationshipRepository.getById(id) ?: throw UserRelationshipException.NotFound()
        return when (status) {
            RelationshipStatus.Accepted, RelationshipStatus.Blocked -> {
                if (relationship.addresseeId != userId) throw UserRelationshipException.Forbidden()
                userRelationshipRepository.updateRelationship(id, status)
            }

            else -> throw UserRelationshipException.SettingPendingNotAllowed()
        }
    }
}
