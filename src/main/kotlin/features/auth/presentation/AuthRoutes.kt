@file:OptIn(ExperimentalUuidApi::class)

package com.haykor.features.auth.presentation

import com.haykor.features.auth.domain.AuthException
import com.haykor.features.auth.domain.ExternalLoginUseCase
import com.haykor.features.auth.domain.LoginUseCase
import com.haykor.features.auth.domain.RefreshTokensUseCase
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

fun Route.authRoutes() {
    val loginUseCase by inject<LoginUseCase>()
    val externalLoginUseCase by inject<ExternalLoginUseCase>()
    val refreshTokensUseCase by inject<RefreshTokensUseCase>()
    val httpClient by inject<HttpClient>()

    /**
     * Tag: Auth
     */
    route("/auth") {
        post("/login") {
            val request = call.receive<LoginRequest>()
            val userAgent = call.request.headers["User-Agent"] ?: "Unknown"
            val userIp = call.request.origin.remoteAddress

            val auth = loginUseCase(request, userIp, userAgent)
            call.response.cookies.append(
                name = "refresh_token",
                value = auth.refreshToken.toString(),
                httpOnly = true,
                secure = true,
                path = "/api/auth",
                maxAge = auth.refreshTokenExpiresIn
            )
            call.respond(
                HttpStatusCode.OK, TokenResponse(
                    auth.accessToken,
                    auth.refreshToken.toString(),
                    auth.accessTokenExpiresIn,
                    auth.refreshTokenExpiresIn
                )
            )
        }
        post("/refresh_tokens") {
            val refreshToken = call.request.cookies["refresh_token"]
                ?: throw AuthException.InvalidToken()
            val userIp = call.request.origin.remoteAddress
            val userAgent = call.request.headers["User-Agent"] ?: "Unknown"
            val auth = refreshTokensUseCase(Uuid.parse(refreshToken), userIp, userAgent)
            call.response.cookies.append(
                name = "refresh_token",
                value = auth.refreshToken.toString(),
                httpOnly = true,
                secure = true,
                path = "/api/auth",
                maxAge = auth.refreshTokenExpiresIn
            )
            call.respond(
                HttpStatusCode.OK, TokenResponse(
                    auth.accessToken,
                    auth.refreshToken.toString(),
                    auth.accessTokenExpiresIn,
                    auth.refreshTokenExpiresIn
                )
            )
        }
        authenticate("auth-oauth-google") {
            get("/login/google") {
                // automatic redirect to Google login page and then to /callback/google
            }
            get("/callback/google") {
                val principal = call.principal<OAuthAccessTokenResponse.OAuth2>() ?: throw AuthException.InvalidToken()
                val userAgent = call.request.headers["User-Agent"] ?: "Unknown"
                val userIp = call.request.origin.remoteAddress

                val googleUser = httpClient.get("https://www.googleapis.com/oauth2/v3/userinfo") {
                    header(HttpHeaders.Authorization, "Bearer ${principal.accessToken}")
                }.body<GoogleUserDTO>()

                val auth = externalLoginUseCase(googleUser, userIp, userAgent)
                call.respond(
                    HttpStatusCode.OK, TokenResponse(
                        auth.accessToken,
                        auth.refreshToken.toString(),
                        auth.accessTokenExpiresIn,
                        auth.refreshTokenExpiresIn
                    )
                )
            }
        }
    }
}