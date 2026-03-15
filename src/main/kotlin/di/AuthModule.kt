package com.haykor.di

import com.haykor.features.auth.data.AuthSessionRepositoryImpl
import com.haykor.features.auth.data.JwtEncryptor
import com.haykor.features.auth.domain.AuthSessionRepository
import com.haykor.features.auth.domain.LoginUseCase
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
    factory { new(::LoginUseCase) }
}
