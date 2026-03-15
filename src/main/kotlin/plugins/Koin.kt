package com.haykor.plugins

import com.haykor.di.appModule
import com.haykor.di.authModule
import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureKoin() {
    install(Koin) {
        slf4jLogger()
        modules(appModule, authModule(environment.config))
    }
}