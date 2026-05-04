package com.haykor.features.eventCategories.domain.repository

import com.haykor.features.eventCategories.domain.model.CreateEventCategoryParams
import com.haykor.features.eventCategories.domain.model.EventCategory

interface EventCategoriesRepository {
    suspend fun create(params: CreateEventCategoryParams): EventCategory
    suspend fun getById(id: Int): EventCategory?
    suspend fun getAllByUser(userId: Int): List<EventCategory>
    suspend fun deleteById(id: Int): Boolean
}
