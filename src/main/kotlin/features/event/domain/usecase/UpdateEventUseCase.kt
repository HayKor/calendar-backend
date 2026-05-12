package com.haykor.features.event.domain.usecase

import com.haykor.features.event.data.model.UpdateEventDbParams
import com.haykor.features.event.domain.model.Event
import com.haykor.features.event.domain.model.EventError
import com.haykor.features.event.domain.repository.EventRepository
import com.haykor.features.event.domain.repository.UpdateEventParams
import com.haykor.features.event.domain.service.RRuleBuilder
import java.time.OffsetDateTime
import java.time.ZoneOffset

// UpdateEventUseCase — updates base event, affects all occurrences
class UpdateEventUseCase(
    private val eventRepository: EventRepository,
) {
    suspend operator fun invoke(userId: Int, eventId: Int, params: UpdateEventParams): Event {
        val event = eventRepository.getById(eventId) ?: throw EventError.NotFound()
        if (event.userId != userId) throw EventError.Forbidden()
        val rruleString = RRuleBuilder.build(params.rrule)
        return eventRepository.update(
            eventId,
            UpdateEventDbParams(
                categoryId = params.categoryId,
                title = params.title,
                description = params.description,
                location = params.location,
                startAt = params.startAt,
                endAt = params.endAt,
                isAllDay = params.isAllDay,
                eventTimezone = params.eventTimezone,
                visibility = params.visibility,
                isRecurring = params.rrule.isRecurring,
                rrule = rruleString,
                recurrenceUntil = params.rrule.until?.let {
                    OffsetDateTime.of(it.year, it.month.ordinal, it.day, 23, 59, 59, 0, ZoneOffset.UTC)
                },
            ),
        ) ?: throw EventError.NotFound()
    }
}
