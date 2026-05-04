package com.haykor.core.di

import com.haykor.features.auth.data.AuthSessionRepositoryImpl
import com.haykor.features.auth.domain.*
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
