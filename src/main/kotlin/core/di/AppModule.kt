package com.haykor.core.di

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
}
