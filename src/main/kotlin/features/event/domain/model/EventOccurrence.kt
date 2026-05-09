package com.haykor.features.event.domain.model

import com.haykor.core.common.domain.Visibility
import kotlinx.datetime.LocalDate
import java.time.OffsetDateTime

data class EventOccurrence(
    val eventId: Int,
    val originalDate: LocalDate,
    val title: String,
    val description: String?,
    val location: String?,
    val startAt: OffsetDateTime,
    val endAt: OffsetDateTime?,
    val isAllDay: Boolean,
    val eventTimezone: String,
    val visibility: Visibility,
    val categoryId: Int?,
    val isRecurring: Boolean,
    val isCancelled: Boolean,
)
