package com.haykor.features.user.domain.model

data class UserRelationship(
    val id: Int,
    val requesterId: Int,
    val addresseeId: Int,
    val status: RelationshipStatus,
)
