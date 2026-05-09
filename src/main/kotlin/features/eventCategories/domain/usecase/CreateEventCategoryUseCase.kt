package com.haykor.features.eventCategories.domain.usecase

import com.haykor.features.eventCategories.domain.model.EventCategory
import com.haykor.features.eventCategories.domain.repository.CreateEventCategoryParams
import com.haykor.features.eventCategories.domain.repository.EventCategoriesRepository

class CreateEventCategoryUseCase(
    private val repository: EventCategoriesRepository,
) {
    suspend operator fun invoke(
        params: CreateEventCategoryParams,
    ): EventCategory = repository.create(
        params,
    )
}
