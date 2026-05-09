package com.haykor.features.event.domain.usecase

import com.haykor.core.util.mapper.toKotlinLocalDate
import com.haykor.features.event.domain.model.Event
import com.haykor.features.event.domain.model.EventOccurrence
import com.haykor.features.event.domain.repository.EventExceptionRepository
import com.haykor.features.event.domain.repository.EventRepository
import com.haykor.features.event.domain.service.RRuleExpander
import java.time.OffsetDateTime

class GetEventsInRangeUseCase(
    private val eventRepository: EventRepository,
    private val eventExceptionRepository: EventExceptionRepository,
    private val rruleExpander: RRuleExpander,
) {
    suspend operator fun invoke(
        userId: Int,
        from: OffsetDateTime,
        to: OffsetDateTime,
    ): List<EventOccurrence> {
        val events = eventRepository.getByUserIdAndRange(userId, from, to)
        val (recurringEvents, regularEvents) = events.partition { it.isRecurring }

        val recurringIds = recurringEvents.map { it.id }
        val exceptions = eventExceptionRepository.getByEventIds(recurringIds, from, to).groupBy { it.eventId }

        val regularOccurrences = regularEvents.map { it.toOccurrence() }
        val recurringOccurrences = recurringEvents.flatMap { event ->
            rruleExpander.expand(event, from, to, exceptions = exceptions[event.id] ?: emptyList())
        }
        return (regularOccurrences + recurringOccurrences).sortedBy { it.startAt }
    }
}

private fun Event.toOccurrence() = EventOccurrence(
    eventId = id,
    categoryId = categoryId,
    originalDate = startAt.toKotlinLocalDate(),
    title = title,
    description = description,
    location = location,
    startAt = startAt,
    endAt = endAt,
    isAllDay = isAllDay,
    eventTimezone = eventTimezone,
    visibility = visibility,
    isRecurring = isRecurring,
    isCancelled = false,
)
