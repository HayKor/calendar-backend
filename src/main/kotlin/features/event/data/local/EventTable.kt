package com.haykor.features.event.data.local

import com.haykor.core.visibility.domain.model.Visibility
import com.haykor.features.eventCategories.data.local.EventCategoryTable
import com.haykor.features.user.data.local.UserTable
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.datetime.CurrentTimestampWithTimeZone
import org.jetbrains.exposed.v1.datetime.timestampWithTimeZone

object EventTable : IntIdTable("events") {
    val user = reference("user_id", UserTable, onDelete = ReferenceOption.CASCADE)
    val category = reference("category_id", EventCategoryTable, onDelete = ReferenceOption.SET_NULL).nullable()

    val title = varchar("title", 255)
    val description = text("description").nullable()
    val location = varchar("location", 255).nullable()
    val eventTimezone = varchar("event_timezone", 50).default("UTC")

    val startAt = timestampWithTimeZone("start_at")
    val endAt = timestampWithTimeZone("end_at").nullable()

    val isAllDay = bool("is_all_day").default(false)
    val visibility = enumeration<Visibility>("visibility")
        .default(Visibility.Friends)

    val isRecurring = bool("is_recurring").default(false)
    val rrule = varchar("rrule", 255).nullable()
    val recurrenceUntil = timestampWithTimeZone("recurrence_until").nullable()

    val createdAt = timestampWithTimeZone("created_at").defaultExpression(CurrentTimestampWithTimeZone)
    val updatedAt = timestampWithTimeZone("updated_at").defaultExpression(CurrentTimestampWithTimeZone)

    init {
        index(false, user, startAt)
        index(false, category)
        // partial index — Exposed doesn't support WHERE clause natively
    }
}
