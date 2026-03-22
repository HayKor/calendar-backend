package com.haykor.plugins

import com.haykor.features.auth.data.JwtEncryptor
import io.ktor.client.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.config.*
import io.ktor.server.response.*
import org.koin.ktor.ext.inject

fun Application.configureAuth(config: ApplicationConfig) {
    configureSecurity()
    configureOAuth(config)
}

private fun Application.configureSecurity() {
    val jwtEncryptor by inject<JwtEncryptor>()

    authentication {
        jwt("auth-jwt") {
            realm = "Access to authorized endpoints"
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

private fun Application.configureOAuth(config: ApplicationConfig) {
    val applicationHttpClient by inject<HttpClient>()

    authentication {
        oauth("auth-oauth-google") {
            urlProvider = { "http://localhost:8080/api/auth/callback/google" }
            providerLookup = {
                OAuthServerSettings.OAuth2ServerSettings(
                    name = "google",
                    authorizeUrl = "https://accounts.google.com/o/oauth2/auth",
                    accessTokenUrl = "https://accounts.google.com/o/oauth2/token",
                    requestMethod = HttpMethod.Post,
                    clientId = config.property("oauth.google.clientId").getString(),
                    clientSecret = config.property("oauth.google.clientSecret").getString(),
                    defaultScopes = listOf("https://www.googleapis.com/auth/userinfo.email", "profile")
                )
            }
            client = applicationHttpClient
        }
    }
}