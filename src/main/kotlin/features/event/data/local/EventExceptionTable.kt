package com.haykor.features.event.data.local

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.datetime.CurrentTimestampWithTimeZone
import org.jetbrains.exposed.v1.datetime.date
import org.jetbrains.exposed.v1.datetime.timestampWithTimeZone

object EventExceptionTable : IntIdTable("event_exceptions") {
    val event = reference("event_id", EventTable, onDelete = ReferenceOption.CASCADE)
    val originalDate = date("original_date")
    val isDeleted = bool("is_deleted").default(false)
    val titleOverride = varchar("title_override", 255).nullable()
    val startAtOverride = timestampWithTimeZone("start_at_override").nullable()
    val endAtOverride = timestampWithTimeZone("end_at_override").nullable()
    val locationOverride = varchar("location_override", 255).nullable()
    val createdAt = timestampWithTimeZone("created_at").defaultExpression(CurrentTimestampWithTimeZone)

    init {
        uniqueIndex(event, originalDate)
    }
}
