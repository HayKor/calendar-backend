@file:OptIn(ExperimentalUuidApi::class)

package com.haykor.features.auth.presentation

import com.haykor.core.common.presentation.requestContext
import com.haykor.features.auth.domain.model.Auth
import com.haykor.features.auth.domain.model.AuthException
import com.haykor.features.auth.domain.service.GoogleIdTokenVerifier
import com.haykor.features.auth.domain.usecase.ExternalLoginUseCase
import com.haykor.features.auth.domain.usecase.LoginUseCase
import com.haykor.features.auth.domain.usecase.RefreshTokensUseCase
import com.haykor.features.auth.presentation.model.GoogleIdTokenRequest
import com.haykor.features.auth.presentation.model.GoogleUserDTO
import com.haykor.features.auth.presentation.model.LoginRequest
import com.haykor.features.auth.presentation.model.TokenResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.auth.*
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
    val googleIdTokenVerifier by inject<GoogleIdTokenVerifier>()

    /**
     * Tag: Auth
     */
    route("/auth") {
        post("/login") {
            val request = call.receive<LoginRequest>()
            val (userIp, userAgent) = call.requestContext()

            call.respondWithTokens(loginUseCase(request, userIp, userAgent))
        }
        post("/login/google/mobile") {
            val request = call.receive<GoogleIdTokenRequest>()
            val (userIp, userAgent) = call.requestContext()

            // Verify the ID token with Google and extract user info
            val googleUser = googleIdTokenVerifier.verify(request.idToken)
                ?: throw AuthException.InvalidToken()

            call.respondWithTokens(externalLoginUseCase(googleUser, userIp, userAgent))
        }
        post("/refresh_tokens") {
            val refreshToken =
                call.request.cookies["refresh_token"]
                    ?: throw AuthException.InvalidToken()
            val (userIp, userAgent) = call.requestContext()
            call.respondWithTokens(refreshTokensUseCase(Uuid.parse(refreshToken), userIp, userAgent))
        }
        authenticate("auth-oauth-google") {
            get("/login/google") {
                // automatic redirect to Google login page and then to /callback/google
            }
            get("/callback/google") {
                val principal = call.principal<OAuthAccessTokenResponse.OAuth2>() ?: throw AuthException.InvalidToken()
                val (userIp, userAgent) = call.requestContext()

                val googleUser =
                    httpClient
                        .get("https://www.googleapis.com/oauth2/v3/userinfo") {
                            header(HttpHeaders.Authorization, "Bearer ${principal.accessToken}")
                        }.body<GoogleUserDTO>()

                call.respondWithTokens(externalLoginUseCase(googleUser, userIp, userAgent))
            }
        }
    }
}

private suspend fun RoutingCall.respondWithTokens(auth: Auth) {
    response.cookies.append(
        name = "refresh_token",
        value = auth.refreshToken.toString(),
        httpOnly = true,
        secure = true,
        path = "/api/auth",
        maxAge = auth.refreshTokenExpiresIn,
    )
    respond(
        HttpStatusCode.OK,
        TokenResponse(
            auth.accessToken,
            auth.refreshToken.toString(),
            auth.accessTokenExpiresIn,
            auth.refreshTokenExpiresIn,
        ),
    )
}
