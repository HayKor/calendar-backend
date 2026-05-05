@file:OptIn(ExperimentalUuidApi::class)

package com.haykor.features.auth.domain.usecase

import com.haykor.features.auth.data.service.JwtEncryptor
import com.haykor.features.auth.domain.model.Auth
import com.haykor.features.auth.domain.model.AuthException
import com.haykor.features.auth.domain.model.CreateAuthSessionParams
import com.haykor.features.auth.domain.repository.AuthSessionRepository
import com.haykor.features.auth.presentation.model.LoginRequest
import com.haykor.features.user.domain.model.UserException
import com.haykor.features.user.domain.repository.UserRepository
import com.haykor.features.user.domain.service.PasswordHasher
import kotlin.uuid.ExperimentalUuidApi

class LoginUseCase(
    private val authSessionRepository: AuthSessionRepository,
    private val userRepository: UserRepository,
    private val passwordHasher: PasswordHasher,
    private val jwtEncryptor: JwtEncryptor,
) {
    suspend operator fun invoke(
        request: LoginRequest,
        userIp: String,
        userAgent: String,
    ): Auth {
        val user = userRepository.findByEmail(request.email) ?: throw UserException.UserNotFound()
        raiseUserPassword(user.hashedPassword, request.password)
        val authSession = authSessionRepository.createSession(CreateAuthSessionParams(user.id, userIp, userAgent))

        return Auth(
            refreshToken = authSession.refreshToken,
            accessToken = jwtEncryptor.encryptAccessToken(user.id),
            refreshTokenExpiresIn = jwtEncryptor.refreshTokenLifetime,
            accessTokenExpiresIn = jwtEncryptor.accessTokenLifetime,
        )
    }

    private fun raiseUserPassword(
        hashedPassword: String?,
        password: String,
    ) {
        if (hashedPassword == null) {
            throw AuthException.InvalidCredentials()
        }
        if (!passwordHasher.check(password, hashedPassword)) {
            throw AuthException.InvalidCredentials()
        }
    }
}
