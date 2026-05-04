package com.haykor.core.di

import com.haykor.features.eventCategories.data.repository.EventCategoriesRepositoryImpl
import com.haykor.features.eventCategories.domain.repository.EventCategoriesRepository
import com.haykor.features.eventCategories.domain.usecase.CreateEventCategoryUseCase
import com.haykor.features.eventCategories.domain.usecase.DeleteEventCategoryUseCase
import com.haykor.features.eventCategories.domain.usecase.GetAllEventCategoriesUseCase
import com.haykor.features.eventCategories.domain.usecase.GetEventCategoryByIdUseCase
import org.koin.core.module.dsl.new
import org.koin.dsl.module

val eventCategoryModule =
    module {
        single<EventCategoriesRepository> { new(::EventCategoriesRepositoryImpl) }

        single { new(::CreateEventCategoryUseCase) }
        single { new(::DeleteEventCategoryUseCase) }
        single { new(::GetAllEventCategoriesUseCase) }
        single { new(::GetEventCategoryByIdUseCase) }
    }
