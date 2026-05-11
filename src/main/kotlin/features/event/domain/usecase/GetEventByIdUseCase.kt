package com.haykor.features.event.domain.usecase

import com.haykor.core.visibility.domain.model.Visibility
import com.haykor.core.visibility.domain.service.VisibilityGate
import com.haykor.core.visibility.domain.usecase.ResolveViewerRelationUseCase
import com.haykor.features.event.domain.model.Event
import com.haykor.features.event.domain.model.EventError
import com.haykor.features.event.domain.repository.EventRepository
import com.haykor.features.eventCategories.domain.repository.EventCategoriesRepository

class GetEventByIdUseCase(
    private val eventRepository: EventRepository,
    private val eventCategoriesRepository: EventCategoriesRepository,
    private val resolveViewerRelation: ResolveViewerRelationUseCase,
) {
    suspend operator fun invoke(requesterId: Int, eventId: Int): Event {
        val event = eventRepository.getById(eventId) ?: throw EventError.NotFound()
        val category = event.categoryId?.let { eventCategoriesRepository.getById(it) }
        val relation = resolveViewerRelation(requesterId, event.userId)
        val canView = VisibilityGate.canView(
            relation = relation,
            eventVisibility = event.visibility,
            categoryVisibility = category?.visibility,
            userGlobalVisibility = Visibility.Public, // TODO: change to settings
        )
        if (!canView) throw EventError.Forbidden()
        return event
    }
}
