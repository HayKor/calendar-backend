package com.haykor.features.event.domain.repository

import kotlinx.datetime.LocalDate
import java.time.OffsetDateTime

data class CreateEventExceptionParams(
    val eventId: Int,
    val originalDate: LocalDate,
    val isDeleted: Boolean = false,
    val titleOverride: String? = null,
    val startAtOverride: OffsetDateTime? = null,
    val endAtOverride: OffsetDateTime? = null,
    val locationOverride: String? = null,
)

data class UpdateEventExceptionParams(
    val isDeleted: Boolean? = null,
    val titleOverride: String? = null,
    val startAtOverride: OffsetDateTime? = null,
    val endAtOverride: OffsetDateTime? = null,
    val locationOverride: String? = null,
)
