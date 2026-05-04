package com.haykor.features.eventCategories.domain.usecase

import com.haykor.features.eventCategories.domain.model.EventCategoryException
import com.haykor.features.eventCategories.domain.repository.EventCategoriesRepository

class DeleteEventCategoryUseCase(
    private val repository: EventCategoriesRepository,
) {
    suspend operator fun invoke(
        id: Int,
        requestingUserId: Int,
    ) {
        val category = repository.getById(id) ?: throw EventCategoryException.NotFound()

        if (category.userId != requestingUserId) throw EventCategoryException.Forbidden()

        repository.deleteById(id)
    }
}
