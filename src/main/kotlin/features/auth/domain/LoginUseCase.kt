package com.haykor.features.auth.domain

import com.haykor.features.auth.data.JwtEncryptor
import com.haykor.features.auth.presentation.LoginRequest
import com.haykor.features.user.domain.PasswordHasher
import com.haykor.features.user.domain.User
import com.haykor.features.user.domain.UserRepository

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
        raiseUserPassword(user, request.password)
        val authSession = authSessionRepository.createSession(CreateAuthSession(user.id ?: -1, userIp, userAgent))

        return Auth(
            refreshToken = jwtEncryptor.encryptToken(authSession.refreshToken, refreshTokenLifetime),
            accessToken = jwtEncryptor.encryptToken(authSession.accessToken, accessTokenLifetime),
            refreshTokenExpiresIn = refreshTokenLifetime,
            accessTokenExpiresIn = accessTokenLifetime
        )
    }

    private fun raiseUserPassword(user: User, password: String) {
        if (!passwordHasher.check(password, user.hashedPassword)) {
            throw AuthException.InvalidCredentials()
        }
    }

}

sealed class AuthException(message: String) : Exception(message) {
    class InvalidCredentials : AuthException("Invalid email or password")
    class UserNotFound : AuthException("No user found with this email")
}