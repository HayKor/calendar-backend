package com.haykor.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.request.*

fun Application.configureCallLogging() {
    install(CallLogging) {
        format { call ->
            val status = call.response.status()
            val path = call.request.path()
            val httpMethod = call.request.httpMethod.value
            "Path: $httpMethod $path, Status: $status"
        }
    }
}