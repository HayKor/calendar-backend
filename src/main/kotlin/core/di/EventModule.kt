package com.haykor.core.di

import com.haykor.features.event.data.repository.EventRepositoryImpl
import com.haykor.features.event.domain.repository.EventRepository
import org.koin.core.module.dsl.new
import org.koin.dsl.module

val eventModule = module {
    single<EventRepository> { new(::EventRepositoryImpl) }
}
