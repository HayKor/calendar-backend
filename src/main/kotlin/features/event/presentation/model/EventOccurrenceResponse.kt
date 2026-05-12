package com.haykor.features.event.presentation.model

import com.haykor.core.visibility.domain.model.Visibility
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime

@Serializable
data class EventOccurrenceResponse(
    val eventId: Int,
    val originalDate: LocalDate,
    val title: String,
    val description: String?,
    val location: String?,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val startAt: OffsetDateTime,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val endAt: OffsetDateTime?,
    val isAllDay: Boolean,
    val eventTimezone: String,
    val visibility: Visibility,
    val categoryId: Int?,
    val isRecurring: Boolean,
    val isCancelled: Boolean,
)
