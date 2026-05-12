package com.haykor.features.event.presentation.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime

@Serializable
data class EventExceptionResponse(
    val id: Int,
    val eventId: Int,
    val originalDate: LocalDate,
    val isDeleted: Boolean,
    val titleOverride: String?,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val startAtOverride: OffsetDateTime?,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val endAtOverride: OffsetDateTime?,
    val locationOverride: String?,
)
