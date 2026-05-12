package com.haykor.features.event.presentation.model

import com.haykor.core.visibility.domain.model.Visibility
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime

@Serializable
data class UpdateEventRequest(
    val categoryId: Int? = null,
    val title: String? = null,
    val description: String? = null,
    val location: String? = null,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val startAt: OffsetDateTime? = null,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val endAt: OffsetDateTime? = null,
    val isAllDay: Boolean? = null,
    val eventTimezone: String? = null,
    val visibility: Visibility? = null,
)
