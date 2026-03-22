package com.haykor.features.auth.domain

import com.haykor.features.auth.data.JwtEncryptor
import com.haykor.features.auth.presentation.GoogleUserDTO
import com.haykor.features.user.domain.CreateUserParams
import com.haykor.features.user.domain.UserRepository
import kotlin.uuid.ExperimentalUuidApi

class ExternalLoginUseCase(
    private val authSessionRepository: AuthSessionRepository,
    private val userRepository: UserRepository,
    private val jwtEncryptor: JwtEncryptor,
) {

    private val accessTokenLifetime = 30L * 60L * 1000L // 30 mins
    private val refreshTokenLifetime = 30L * 24L * 60L * 60L * 1000L // 30 days // TODO: change to env

    @OptIn(ExperimentalUuidApi::class)
    suspend fun execute(googleUserDTO: GoogleUserDTO, userIp: String, userAgent: String): Auth {
        val user = userRepository.findByEmail(googleUserDTO.email) ?: userRepository.create(
            CreateUserParams(
                email = googleUserDTO.email,
                name = googleUserDTO.name,
                isVerified = true
            )
        )
        val authSession = authSessionRepository.createSession(
            CreateAuthSession(
                userId = user.id,
                userIp = userIp,
                userAgent = userAgent
            )
        )
        return Auth(
            refreshToken = jwtEncryptor.encryptToken(authSession.refreshToken, refreshTokenLifetime),
            accessToken = jwtEncryptor.encryptToken(authSession.accessToken, accessTokenLifetime),
            refreshTokenExpiresIn = refreshTokenLifetime,
            accessTokenExpiresIn = accessTokenLifetime
        )
    }
}