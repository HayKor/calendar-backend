package com.haykor.core.health.presentation

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.healthCheckRoutes() {

    /**
     * Tag: Healthcheck
     */
    get("/health") {
        call.respond(
            HttpStatusCode.OK,
        )
    }
}
