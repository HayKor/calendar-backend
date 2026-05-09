package com.haykor.features.event.domain.usecase

import com.haykor.features.event.domain.model.Event
import com.haykor.features.event.domain.model.EventError
import com.haykor.features.event.domain.repository.EventRepository

class GetEventByIdUseCase(
    private val eventRepository: EventRepository,
) {
    suspend operator fun invoke(requesterId: Int, eventId: Int): Event {
        val event = eventRepository.getById(eventId) ?: throw EventError.NotFound()
        if (event.id != requesterId) throw EventError.Forbidden() // TODO: fix this to visibility check
        return event
    }
}
