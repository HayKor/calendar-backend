package com.haykor.features.event.domain.usecase

import com.haykor.features.event.domain.model.Event
import com.haykor.features.event.domain.repository.CreateEventParams
import com.haykor.features.event.domain.repository.EventRepository

class CreateEventUseCase(
    private val eventRepository: EventRepository,
) {
    suspend operator fun invoke(params: CreateEventParams): Event = eventRepository.create(params)
}
