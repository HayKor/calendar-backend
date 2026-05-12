package com.haykor.features.event.data.model

import com.haykor.core.visibility.domain.model.Visibility
import java.time.OffsetDateTime

data class UpdateEventDbParams(
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
