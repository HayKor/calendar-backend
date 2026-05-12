package com.haykor.features.event.domain.usecase

import com.haykor.features.event.data.model.CreateEventDbParams
import com.haykor.features.event.domain.model.Event
import com.haykor.features.event.domain.repository.CreateEventParams
import com.haykor.features.event.domain.repository.EventRepository
import com.haykor.features.event.domain.service.RRuleBuilder
import java.time.OffsetDateTime
import java.time.ZoneOffset

class CreateEventUseCase(
    private val eventRepository: EventRepository,
) {
    suspend operator fun invoke(params: CreateEventParams): Event {
        val rruleString = RRuleBuilder.build(params.rrule)
        return eventRepository.create(
            CreateEventDbParams(
                userId = params.userId,
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
        )
    }
}
