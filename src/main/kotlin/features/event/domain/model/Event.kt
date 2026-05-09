package com.haykor.features.event.domain.model

import com.haykor.core.common.domain.Visibility
import java.time.OffsetDateTime

data class Event(
    val id: Int,
    val userId: Int,
    val categoryId: Int? = null,
    val title: String,
    val description: String? = null,
    val location: String? = null,
    val eventTimezone: String,
    val startAt: OffsetDateTime,
    val endAt: OffsetDateTime? = null,
    val isAllDay: Boolean,
    val visibility: Visibility,
    val isRecurring: Boolean,
    val rrule: String? = null,
    val recurrenceUntil: OffsetDateTime? = null,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime,
)
