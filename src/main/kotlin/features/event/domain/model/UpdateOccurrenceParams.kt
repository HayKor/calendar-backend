package com.haykor.features.event.domain.model

import java.time.OffsetDateTime

data class UpdateOccurrenceParams(
    val title: String? = null,
    val startAt: OffsetDateTime? = null,
    val endAt: OffsetDateTime? = null,
    val location: String? = null,
)
