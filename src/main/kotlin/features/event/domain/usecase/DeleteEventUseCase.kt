package com.haykor.features.event.domain.usecase

import com.haykor.features.event.domain.model.EventError
import com.haykor.features.event.domain.repository.EventRepository

class DeleteEventUseCase(
    private val eventRepository: EventRepository,
) {
    suspend operator fun invoke(userId: Int, eventId: Int) {
        val event = eventRepository.getById(eventId) ?: throw EventError.NotFound()
        if (event.userId != userId) throw EventError.Forbidden()
        eventRepository.delete(eventId)
    }
}
