package com.haykor.features.user.presentation.model

import com.haykor.features.user.domain.model.RelationshipStatus
import com.haykor.features.user.domain.model.UserRelationship
import kotlinx.serialization.Serializable

@Serializable
data class UpdateRelationshipRequest(val status: RelationshipStatus)

@Serializable
data class UserRelationshipResponse(
    val id: Int,
    val requesterId: Int,
    val addresseeId: Int,
    val status: RelationshipStatus,
)

fun UserRelationship.toResponse() = UserRelationshipResponse(
    id = id,
    requesterId = requesterId,
    addresseeId = addresseeId,
    status = status,
)
