package com.haykor.features.auth.presentation

import com.haykor.features.auth.domain.LoginUseCase
import io.ktor.http.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.authRoutes() {
    val loginUseCase by inject<LoginUseCase>()

    /**
     * Tag: Auth
     */
    route("/auth") {
        post("/login") {
            val request = call.receive<LoginRequest>()
            val userAgent = call.request.headers["User-Agent"] ?: "Unknown"
            val userIp = call.request.origin.remoteAddress

            val auth = loginUseCase.execute(request, userIp, userAgent)
            call.response.cookies.append(
                name = "refresh_token",
                value = auth.refreshToken,
                httpOnly = true,
                secure = true,
                path = "/api/auth",
                maxAge = auth.refreshTokenExpiresIn
            )
            call.respond(
                HttpStatusCode.OK, TokenResponse(
                    auth.accessToken,
                    auth.refreshToken,
                    auth.accessTokenExpiresIn,
                    auth.refreshTokenExpiresIn
                )
            )
        }
    }
}