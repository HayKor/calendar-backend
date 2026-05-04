package com.haykor.features.eventCategories.domain.usecase

import com.haykor.features.eventCategories.domain.model.EventCategory
import com.haykor.features.eventCategories.domain.model.EventCategoryException
import com.haykor.features.eventCategories.domain.repository.EventCategoriesRepository

class GetEventCategoryByIdUseCase(
    private val repository: EventCategoriesRepository,
) {
    suspend operator fun invoke(
        id: Int,
        requestingUserId: Int,
    ): EventCategory {
        val category = repository.getById(id) ?: throw EventCategoryException.NotFound()

        if (category.userId != requestingUserId) throw EventCategoryException.Forbidden()

        return category
    }
}
