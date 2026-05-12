package com.haykor.features.event.presentation.model

import com.haykor.core.visibility.domain.model.Visibility
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime

@Serializable
data class EventResponse(
    val id: Int,
    val userId: Int,
    val categoryId: Int?,
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
    val isRecurring: Boolean,
    val rrule: String?,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val createdAt: OffsetDateTime,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val updatedAt: OffsetDateTime,
)
