package com.haykor.features.event.presentation.model

import com.haykor.core.visibility.domain.model.Visibility
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime

@Serializable
data class CreateEventRequest(
    val categoryId: Int? = null,
    val title: String,
    val description: String? = null,
    val location: String? = null,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val startAt: OffsetDateTime,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val endAt: OffsetDateTime? = null,
    val isAllDay: Boolean = false,
    val eventTimezone: String = "UTC",
    val visibility: Visibility = Visibility.Friends,
)
