package com.haykor.core.di

import com.haykor.features.auth.data.JwtEncryptor
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.config.*
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.koin.dsl.module
import org.koin.dsl.onClose

fun appModule(config: ApplicationConfig) = module {
    single {
        R2dbcDatabase.connect(
            url = config.property("db.url").getString(),
            user = config.property("db.user").getString(),
            password = config.property("db.password").getString()
        )
    }
    single {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }
    } onClose { it?.close() }
    single {
        JwtEncryptor(
            secret = config.property("jwt.secret").getString(),
            issuer = config.property("jwt.issuer").getString(),
            audience = config.property("jwt.audience").getString(),
            accessTokenLifetime = config.property("jwt.access_token_lifetime_minutes")
                .getAs<Long>() * 60L * 1000L,
            refreshTokenLifetime = config.property("jwt.refresh_token_lifetime_days")
                .getAs<Long>() * 24L * 60L * 60L * 1000L,
        )
    }
}
