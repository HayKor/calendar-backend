package com.haykor.plugins

import com.haykor.core.exception.AppException
import com.haykor.core.presentation.ErrorResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*

fun Application.configureStatusPages() {
    install(StatusPages) {
        // This catches NotFoundException, UnauthorizedException, etc.
        exception<AppException> { call, cause ->
            call.respond(
                status = cause.statusCode,
                message = ErrorResponse(
                    error = cause.message,
                    path = call.request.path(),
                    status = cause.statusCode.value,
                )
            )
        }
        exception<Throwable> { call, cause ->
            // TODO: Log 'cause.stackTraceToString()'
            println("CRITICAL ERROR: ${cause.message}")

            call.respond(
                status = HttpStatusCode.InternalServerError,
                message = ErrorResponse(
                    error = "An unexpected internal server error occurred.",
                    path = call.request.path(),
                    status = 500
                )
            )
        }
    }
}