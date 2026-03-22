package com.haykor.features.auth.domain

import com.haykor.features.auth.data.JwtEncryptor
import com.haykor.features.auth.presentation.GoogleUserDTO
import com.haykor.features.user.domain.CreateUserParams
import com.haykor.features.user.domain.UserRepository
import com.haykor.features.user.domain.UserSocialsRepository
import kotlin.uuid.ExperimentalUuidApi

class ExternalLoginUseCase(
    private val authSessionRepository: AuthSessionRepository,
    private val userRepository: UserRepository,
    private val userSocialsRepository: UserSocialsRepository,
    private val jwtEncryptor: JwtEncryptor,
) {

    private val accessTokenLifetime = 30L * 60L * 1000L // 30 mins
    private val refreshTokenLifetime = 30L * 24L * 60L * 60L * 1000L // 30 days // TODO: change to env

    @OptIn(ExperimentalUuidApi::class)
    suspend fun execute(googleUserDTO: GoogleUserDTO, userIp: String, userAgent: String): Auth {
        // 1. Try to find the user by their Google ID directly
        val user = userRepository.findBySocials("google", googleUserDTO.id)
            ?: userRepository.findByEmail(googleUserDTO.email)?.also {
                // 2. Found by email? Link the social ID so next time step 1 works
                userSocialsRepository.assignSocialsToUser(it, "google", googleUserDTO.id)
            }
            ?: userRepository.create(
                CreateUserParams(googleUserDTO.email, googleUserDTO.name, isVerified = true)
            ).also {
                // 3. Brand new? Create and link
                userSocialsRepository.assignSocialsToUser(it, "google", googleUserDTO.id)
            }
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