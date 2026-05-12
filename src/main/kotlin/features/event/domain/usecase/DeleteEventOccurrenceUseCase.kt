package com.haykor.features.event.domain.usecase

import com.haykor.features.event.domain.model.EventError
import com.haykor.features.event.domain.repository.CreateEventExceptionParams
import com.haykor.features.event.domain.repository.EventExceptionRepository
import com.haykor.features.event.domain.repository.EventRepository
import com.haykor.features.event.domain.repository.UpdateEventExceptionParams
import kotlinx.datetime.LocalDate

// DeleteEventOccurrenceUseCase — cancels a single occurrence
class DeleteEventOccurrenceUseCase(
    private val eventRepository: EventRepository,
    private val eventExceptionRepository: EventExceptionRepository,
) {
    suspend operator fun invoke(userId: Int, eventId: Int, originalDate: LocalDate) {
        val event = eventRepository.getById(eventId) ?: throw EventError.NotFound()
        if (event.userId != userId) throw EventError.Forbidden()
        if (!event.isRecurring) throw EventError.NotRecurring()

        // check if event exception for this date already exists
        val existing = eventExceptionRepository.getByEventIdAndDate(eventId, originalDate)
        if (existing != null) {
            eventExceptionRepository.update(
                eventId,
                originalDate,
                UpdateEventExceptionParams(isDeleted = true),
            )
        } else {
            eventExceptionRepository.create(
                CreateEventExceptionParams(
                    eventId = eventId,
                    originalDate = originalDate,
                    isDeleted = true,
                ),
            )
        }
    }
}
