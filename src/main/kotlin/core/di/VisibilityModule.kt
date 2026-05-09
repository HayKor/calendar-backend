package com.haykor.core.di

import com.haykor.core.visibility.domain.usecase.ResolveViewerRelationUseCase
import org.koin.core.module.dsl.new
import org.koin.dsl.module

val visibilityModule = module {
    single { new(::ResolveViewerRelationUseCase) }
}
