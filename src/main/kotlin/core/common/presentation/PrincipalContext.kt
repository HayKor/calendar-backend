package com.haykor.core.common.presentation

import com.haykor.core.exception.BadRequest
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.routing.*

data class PrincipalContext(val userId: Int)

fun RoutingContext.principalContext(): PrincipalContext {
    val principal = call.principal<JWTPrincipal>()
        ?: error("principalContext() called outside authenticate block")
    val userId = principal.payload.subject?.toIntOrNull()
        ?: throw BadRequest("Invalid session token")
    return PrincipalContext(userId = userId)
}
