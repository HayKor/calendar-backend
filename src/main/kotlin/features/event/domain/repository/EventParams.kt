package com.haykor.features.event.domain.repository

import com.haykor.core.visibility.domain.model.Visibility
import com.haykor.features.event.domain.model.RRuleInput
import java.time.OffsetDateTime

data class CreateEventParams(
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
    val rrule: RRuleInput,
)

data class UpdateEventParams(
    val categoryId: Int?,
    val title: String?,
    val description: String?,
    val location: String?,
    val startAt: OffsetDateTime?,
    val endAt: OffsetDateTime?,
    val isAllDay: Boolean?,
    val eventTimezone: String?,
    val visibility: Visibility?,
    val isRecurring: Boolean?,
    val rrule: String?,
    val recurrenceUntil: OffsetDateTime?,
)
