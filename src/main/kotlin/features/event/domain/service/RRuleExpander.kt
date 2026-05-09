package com.haykor.features.event.domain.service

import com.haykor.features.event.domain.model.Event
import com.haykor.features.event.domain.model.EventException
import com.haykor.features.event.domain.model.EventOccurrence
import java.time.OffsetDateTime

interface RRuleExpander {
    fun expand(
        event: Event,
        from: OffsetDateTime,
        to: OffsetDateTime,
        exceptions: List<EventException>,
    ): List<EventOccurrence>
}
