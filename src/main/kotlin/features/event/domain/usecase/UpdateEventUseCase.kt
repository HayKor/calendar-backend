package com.haykor.features.event.domain.usecase

import com.haykor.features.event.domain.model.Event
import com.haykor.features.event.domain.model.EventError
import com.haykor.features.event.domain.repository.EventRepository
import com.haykor.features.event.domain.repository.UpdateEventParams

// UpdateEventUseCase — updates base event, affects all occurrences
class UpdateEventUseCase(
    private val eventRepository: EventRepository,
) {
    suspend operator fun invoke(userId: Int, eventId: Int, params: UpdateEventParams): Event {
        val event = eventRepository.getById(eventId) ?: throw EventError.NotFound()
        if (event.userId != userId) throw EventError.Forbidden()
        return eventRepository.update(eventId, params) ?: throw EventError.NotFound()
    }
}
