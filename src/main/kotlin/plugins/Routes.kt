package com.haykor.plugins

import com.haykor.features.auth.presentation.authRoutes
import com.haykor.features.user.presentation.userRoutes
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRoutes() {
    routing {
        route("/api") {
            userRoutes()
            authRoutes()
        }
    }
}
