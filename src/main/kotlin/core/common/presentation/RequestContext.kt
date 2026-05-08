package com.haykor.core.common.presentation

import io.ktor.server.plugins.*
import io.ktor.server.routing.*

data class RequestContext(val userIp: String, val userAgent: String)

fun RoutingCall.requestContext() = RequestContext(
    userIp = request.origin.remoteAddress,
    userAgent = request.headers["User-Agent"] ?: "Unknown",
)
