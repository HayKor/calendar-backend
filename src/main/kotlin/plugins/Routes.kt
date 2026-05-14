package com.haykor.plugins

import com.haykor.core.health.presentation.healthCheckRoutes
import com.haykor.features.auth.presentation.routes.authRoutes
import com.haykor.features.event.presentation.routes.util.eventRoutes
import com.haykor.features.eventCategories.presentation.routes.eventCategoriesRoutes
import com.haykor.features.user.presentation.routes.userRelationshipRoutes
import com.haykor.features.user.presentation.routes.userRoutes
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRoutes() {
    routing {
        route("/api") {
            healthCheckRoutes()
            userRoutes()
            userRelationshipRoutes()
            authRoutes()
            eventRoutes()
            eventCategoriesRoutes()
        }
    }
}
