package com.haykor.features.event.domain.usecase

import com.haykor.features.event.domain.model.EventError
import com.haykor.features.event.domain.model.EventException
import com.haykor.features.event.domain.model.UpdateOccurrenceParams
import com.haykor.features.event.domain.repository.CreateEventExceptionParams
import com.haykor.features.event.domain.repository.EventExceptionRepository
import com.haykor.features.event.domain.repository.EventRepository
import com.haykor.features.event.domain.repository.UpdateEventExceptionParams
import kotlinx.datetime.LocalDate

// Patches a single occurrence via EventException
/**
 * Patches a single occurrence via EventException
 *
 * @property eventRepository
 * @property eventExceptionRepository
 */
class UpdateEventOccurrenceUseCase(
    private val eventRepository: EventRepository,
    private val eventExceptionRepository: EventExceptionRepository,
) {
    suspend operator fun invoke(
        userId: Int,
        eventId: Int,
        originalDate: LocalDate,
        params: UpdateOccurrenceParams,
    ): EventException {
        val event = eventRepository.getById(eventId) ?: throw EventError.NotFound()
        if (event.userId != userId) throw EventError.Forbidden()
        if (!event.isRecurring) throw EventError.NotRecurring()

        val existing = eventExceptionRepository.getByEventIdAndDate(eventId, originalDate)
        return if (existing != null) {
            eventExceptionRepository.update(
                eventId,
                originalDate,
                UpdateEventExceptionParams(
                    titleOverride = params.title,
                    startAtOverride = params.startAt,
                    endAtOverride = params.endAt,
                    locationOverride = params.location,
                ),
            ) ?: throw EventError.NotFound()
        } else {
            eventExceptionRepository.create(
                CreateEventExceptionParams(
                    eventId = eventId,
                    originalDate = originalDate,
                    titleOverride = params.title,
                    startAtOverride = params.startAt,
                    endAtOverride = params.endAt,
                    locationOverride = params.location,
                ),
            )
        }
    }
}
