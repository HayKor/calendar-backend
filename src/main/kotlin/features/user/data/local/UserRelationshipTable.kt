package com.haykor.features.user.data.local

import com.haykor.features.user.domain.model.RelationshipStatus
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.datetime.CurrentTimestampWithTimeZone
import org.jetbrains.exposed.v1.datetime.timestampWithTimeZone

object UserRelationshipTable : IntIdTable("user_relationships") {
    val requester = reference("requester_id", UserTable, ReferenceOption.CASCADE)
    val addressee = reference("addressee_id", UserTable, ReferenceOption.CASCADE)
    val status = enumeration<RelationshipStatus>("status").default(RelationshipStatus.Pending)
    val createdAt = timestampWithTimeZone("created_at").defaultExpression(CurrentTimestampWithTimeZone)
    val updatedAt = timestampWithTimeZone("updated_at").defaultExpression(CurrentTimestampWithTimeZone)

    init {
        uniqueIndex(requester, addressee)
    }
}
