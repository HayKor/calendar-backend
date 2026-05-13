package com.haykor.features.event.presentation.model

import com.haykor.core.visibility.domain.model.Visibility
import io.ktor.openapi.*
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime

@Serializable
data class CreateEventRequest(
    val categoryId: Int? = null,
    val title: String,
    val description: String? = null,
    val location: String? = null,
    @Serializable(with = OffsetDateTimeSerializer::class)
    @JsonSchema.Format("date-time")
    val startAt: OffsetDateTime,
    @Serializable(with = OffsetDateTimeSerializer::class)
    @JsonSchema.Format("date-time")
    val endAt: OffsetDateTime? = null,
    val isAllDay: Boolean = false,
    val eventTimezone: String = "UTC",
    val visibility: Visibility = Visibility.Friends,
)
