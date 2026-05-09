package com.haykor.core.di

import com.haykor.features.event.data.repository.EventExceptionRepositoryImpl
import com.haykor.features.event.data.repository.EventRepositoryImpl
import com.haykor.features.event.data.service.RRuleExpanderImpl
import com.haykor.features.event.domain.repository.EventExceptionRepository
import com.haykor.features.event.domain.repository.EventRepository
import com.haykor.features.event.domain.service.RRuleExpander
import com.haykor.features.event.domain.usecase.GetEventsInRangeUseCase
import org.koin.core.module.dsl.new
import org.koin.dsl.module

val eventModule = module {
    single<EventRepository> { new(::EventRepositoryImpl) }
    single<EventExceptionRepository> { new(::EventExceptionRepositoryImpl) }
    single<RRuleExpander> { new(::RRuleExpanderImpl) }

    single { new(::GetEventsInRangeUseCase) }
}
