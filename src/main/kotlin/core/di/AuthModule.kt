package com.haykor.core.di

import com.haykor.features.auth.data.AuthSessionRepositoryImpl
import com.haykor.features.auth.data.JwtEncryptor
import com.haykor.features.auth.domain.AuthSessionRepository
import com.haykor.features.auth.domain.ExternalLoginUseCase
import com.haykor.features.auth.domain.LoginUseCase
import com.haykor.features.auth.domain.RefreshTokensUseCase
import io.ktor.server.config.*
import org.koin.core.module.dsl.new
import org.koin.dsl.module

fun authModule(config: ApplicationConfig) = module {
    single {
        JwtEncryptor(
            secret = config.property("jwt.secret").getString(),
            issuer = config.property("jwt.issuer").getString(),
            audience = config.property("jwt.audience").getString()
        )
    }

    single<AuthSessionRepository> { AuthSessionRepositoryImpl(get()) }
    single { new(::LoginUseCase) }
    single { new(::ExternalLoginUseCase) }
    single { new(::RefreshTokensUseCase) }
}
