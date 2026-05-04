package com.haykor.features.eventCategories.domain.usecase

import com.haykor.features.eventCategories.domain.model.EventCategory
import com.haykor.features.eventCategories.domain.repository.EventCategoriesRepository

class GetAllEventCategoriesUseCase(
    private val repository: EventCategoriesRepository,
) {
    suspend operator fun invoke(userId: Int): List<EventCategory> = repository.getAllByUser(userId)
}
