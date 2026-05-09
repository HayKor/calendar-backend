package com.haykor.features.user.domain.repository

import com.haykor.features.user.domain.model.RelationshipStatus
import com.haykor.features.user.domain.model.UserRelationship

interface UserRelationshipRepository {
    suspend fun sendRequest(requesterId: Int, addresseeId: Int): UserRelationship
    suspend fun getById(id: Int): UserRelationship?
    suspend fun getRelationships(userId: Int): List<UserRelationship>
    suspend fun getRelationshipBetween(requesterId: Int, addresseeId: Int): UserRelationship?
    suspend fun updateRelationship(id: Int, status: RelationshipStatus): UserRelationship?
}
