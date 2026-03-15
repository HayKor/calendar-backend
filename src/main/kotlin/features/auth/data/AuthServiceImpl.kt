package com.haykor.features.auth.data

import com.haykor.features.auth.domain.Auth
import com.haykor.features.auth.domain.AuthService
import com.haykor.features.auth.domain.AuthSessionRepository
import com.haykor.features.auth.domain.CreateAuthSession
import com.haykor.features.user.domain.PasswordHasher
import com.haykor.features.user.domain.User
import com.haykor.features.user.domain.UserRepository

class AuthServiceImpl(
    private val authSessionRepository: AuthSessionRepository,
    private val userRepository: UserRepository,
    private val passwordHasher: PasswordHasher,
    private val jwtEncryptor: JwtEncryptor,
) : AuthService {

    private val accessTokenLifetime = 30L * 60L * 1000L // 30 mins
    private val refreshTokenLifetime = 30L * 24L * 60L * 60L * 1000L // 30 days // TODO: change to env

    override suspend fun createTokenPair(email: String, password: String, userIp: String, userAgent: String): Auth {
        val user = userRepository.findByEmail(email) ?: throw Exception("User not found")
        raiseUserPassword(user, password)
        val authSession = authSessionRepository.createSession(CreateAuthSession(user.id ?: -1, userIp, userAgent))

        return Auth(
            refreshToken = jwtEncryptor.encryptToken(authSession.refreshToken, refreshTokenLifetime),
            accessToken = jwtEncryptor.encryptToken(authSession.accessToken, accessTokenLifetime),
            refreshTokenExpiresIn = refreshTokenLifetime,
            accessTokenExpiresIn = accessTokenLifetime,
        )
    }

    private fun raiseUserPassword(user: User, password: String) {
        if (!passwordHasher.check(password, user.hashedPassword)) {
            throw Exception("Wrong password")
        }
    }

}