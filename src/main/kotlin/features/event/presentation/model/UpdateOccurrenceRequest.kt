package com.haykor.features.event.presentation.model

import kotlinx.serialization.Serializable
import java.time.OffsetDateTime

@Serializable
data class UpdateOccurrenceRequest(
    val title: String? = null,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val startAt: OffsetDateTime? = null,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val endAt: OffsetDateTime? = null,
    val location: String? = null,
)
