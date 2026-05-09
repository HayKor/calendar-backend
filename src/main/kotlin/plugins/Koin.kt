package com.haykor.plugins

import com.haykor.core.di.appModule
import com.haykor.core.di.authModule
import com.haykor.core.di.eventCategoryModule
import com.haykor.core.di.eventModule
import com.haykor.core.di.userModule
import com.haykor.core.di.visibilityModule
import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureKoin() {
    install(Koin) {
        slf4jLogger()
        modules(
            appModule(environment.config),
            authModule(environment.config),
            userModule,
            eventCategoryModule,
            eventModule,
            visibilityModule,
        )
    }
}
