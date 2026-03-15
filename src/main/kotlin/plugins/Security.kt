package com.haykor.plugins

import com.haykor.features.auth.data.JwtEncryptor
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import org.koin.ktor.ext.inject

fun Application.configureSecurity() {
    val jwtEncryptor by inject<JwtEncryptor>()

    install(Authentication) {
        jwt("auth-jwt") {
            realm = "Access to 'users'"
            verifier(jwtEncryptor.verifier)
            validate { credential ->
                if (credential.payload.getClaim("token").asString() != null) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
            challenge { defaultScheme, realm ->
                call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
            }
        }
    }
}
