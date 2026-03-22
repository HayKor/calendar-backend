@file:OptIn(ExperimentalUuidApi::class)

package com.haykor.features.auth.domain

import com.haykor.features.auth.data.JwtEncryptor
import com.haykor.features.auth.presentation.LoginRequest
import com.haykor.features.user.domain.PasswordHasher
import com.haykor.features.user.domain.UserRepository
import kotlin.uuid.ExperimentalUuidApi

class LoginUseCase(
    private val authSessionRepository: AuthSessionRepository,
    private val userRepository: UserRepository,
    private val passwordHasher: PasswordHasher,
    private val jwtEncryptor: JwtEncryptor,
) {

    private val accessTokenLifetime = 30L * 60L * 1000L // 30 mins
    private val refreshTokenLifetime = 30L * 24L * 60L * 60L * 1000L // 30 days // TODO: change to env

    suspend fun execute(request: LoginRequest, userIp: String, userAgent: String): Auth {
        val user = userRepository.findByEmail(request.email) ?: throw AuthException.UserNotFound()
        raiseUserPassword(user.hashedPassword, request.password)
        val authSession = authSessionRepository.createSession(CreateAuthSession(user.id, userIp, userAgent))

        return Auth(
            refreshToken = jwtEncryptor.encryptToken(authSession.refreshToken, refreshTokenLifetime),
            accessToken = jwtEncryptor.encryptToken(authSession.accessToken, accessTokenLifetime),
            refreshTokenExpiresIn = refreshTokenLifetime,
            accessTokenExpiresIn = accessTokenLifetime
        )
    }

    private fun raiseUserPassword(hashedPassword: String?, password: String) {
        if (hashedPassword == null) {
            throw AuthException.InvalidCredentials()
        }
        if (!passwordHasher.check(password, hashedPassword)) {
            throw AuthException.InvalidCredentials()
        }
    }
}