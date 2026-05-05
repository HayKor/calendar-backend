package com.haykor.core.di

import com.haykor.features.auth.data.repository.AuthSessionRepositoryImpl
import com.haykor.features.auth.domain.*
import com.haykor.features.auth.domain.repository.AuthSessionRepository
import com.haykor.features.auth.domain.service.GoogleIdTokenVerifier
import com.haykor.features.auth.domain.usecase.ExternalLoginUseCase
import com.haykor.features.auth.domain.usecase.LoginUseCase
import com.haykor.features.auth.domain.usecase.RefreshTokensUseCase
import io.ktor.server.config.*
import org.koin.core.module.dsl.new
import org.koin.dsl.module

fun authModule(config: ApplicationConfig) = module {
    single<AuthSessionRepository> { AuthSessionRepositoryImpl(get()) }

    single { new(::GoogleIdTokenVerifier) }
    single { new(::LoginUseCase) }
    single { new(::ExternalLoginUseCase) }
    single { new(::RefreshTokensUseCase) }
}
