package com.haykor.features.event.data.model

import com.haykor.core.visibility.domain.model.Visibility
import java.time.OffsetDateTime

data class CreateEventDbParams(
    val userId: Int,
    val categoryId: Int? = null,
    val title: String,
    val description: String? = null,
    val location: String? = null,
    val startAt: OffsetDateTime,
    val endAt: OffsetDateTime? = null,
    val isAllDay: Boolean,
    val eventTimezone: String,
    val visibility: Visibility = Visibility.Friends,
    val isRecurring: Boolean,
    val rrule: String? = null,
    val recurrenceUntil: OffsetDateTime? = null,
)
