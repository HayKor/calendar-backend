package com.haykor.features.event.domain.model

import kotlinx.datetime.LocalDate
import java.time.OffsetDateTime

data class EventException(
    val id: Int,
    val eventId: Int,
    val originalDate: LocalDate,
    val isDeleted: Boolean,
    val titleOverride: String?,
    val startAtOverride: OffsetDateTime?,
    val endAtOverride: OffsetDateTime?,
    val locationOverride: String?,
    val createdAt: OffsetDateTime,
)
