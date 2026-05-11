package com.haykor.features.user.data.repository

import com.haykor.features.user.data.local.UserRelationshipTable
import com.haykor.features.user.domain.model.RelationshipStatus
import com.haykor.features.user.domain.model.UserRelationship
import com.haykor.features.user.domain.repository.UserRelationshipRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.or
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.insertReturning
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.r2dbc.updateReturning
import java.time.OffsetDateTime

class UserRelationshipRepositoryImpl(
    private val db: R2dbcDatabase,
) : UserRelationshipRepository {

    override suspend fun sendRequest(
        requesterId: Int,
        addresseeId: Int,
    ): UserRelationship = suspendTransaction(db) {
        UserRelationshipTable.insertReturning {
            it[requester] = requesterId
            it[addressee] = addresseeId
            // request is pending by default
        }.map { it.toUserRelationship() }.single()
    }

    override suspend fun getById(id: Int): UserRelationship? = suspendTransaction(db) {
        UserRelationshipTable.selectAll()
            .where { UserRelationshipTable.id eq id }
            .map { it.toUserRelationship() }
            .singleOrNull()
    }

    /**
     * Get all relationships where the [userId] is mentioned
     *
     * @param userId whether the requester or the addressee
     * @return relationships
     */
    override suspend fun getRelationships(userId: Int): List<UserRelationship> = suspendTransaction(db) {
        UserRelationshipTable.selectAll()
            .where {
                ((UserRelationshipTable.requester eq userId) or (UserRelationshipTable.addressee eq userId))
            }
            .map { it.toUserRelationship() }
            .toList()
    }

    override suspend fun getRelationshipBetween(
        requesterId: Int,
        addresseeId: Int,
    ): UserRelationship? = suspendTransaction(db) {
        UserRelationshipTable.selectAll()
            .where {
                (
                    ((UserRelationshipTable.requester eq requesterId) and (UserRelationshipTable.addressee eq addresseeId))
                        or ((UserRelationshipTable.requester eq addresseeId) and (UserRelationshipTable.addressee eq requesterId))
                    )
            }
            .map { it.toUserRelationship() }
            .singleOrNull()
    }

    override suspend fun updateRelationship(id: Int, status: RelationshipStatus): UserRelationship = suspendTransaction(db) {
        UserRelationshipTable.updateReturning(
            where = { UserRelationshipTable.id eq id },
        ) {
            it[this.status] = status
            it[updatedAt] = OffsetDateTime.now()
        }.single().toUserRelationship()
    }

    private fun ResultRow.toUserRelationship() = UserRelationship(
        id = this[UserRelationshipTable.id].value,
        requesterId = this[UserRelationshipTable.requester].value,
        addresseeId = this[UserRelationshipTable.addressee].value,
        status = this[UserRelationshipTable.status],
    )
}
