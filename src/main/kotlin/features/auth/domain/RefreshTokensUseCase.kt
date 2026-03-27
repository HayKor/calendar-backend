@file:OptIn(ExperimentalUuidApi::class)

package com.haykor.features.auth.domain

import com.haykor.features.auth.data.JwtEncryptor
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class RefreshTokensUseCase(
    private val authSessionRepository: AuthSessionRepository,
    private val jwtEncryptor: JwtEncryptor,
) {
    suspend operator fun invoke(
        refreshToken: Uuid,
        userIp: String,
        userAgent: String,
    ): Auth {
        val session =
            authSessionRepository.updateAuthSession(
                refreshToken,
                UpdateSessionParams(
                    refreshToken = Uuid.random(),
                    userIp = userIp,
                    userAgent = userAgent,
                ),
            ) ?: throw AuthException.InvalidToken()
        return Auth(
            refreshToken = session.refreshToken,
            accessToken = jwtEncryptor.encryptAccessToken(session.userId),
            refreshTokenExpiresIn = jwtEncryptor.refreshTokenLifetime,
            accessTokenExpiresIn = jwtEncryptor.accessTokenLifetime,
        )
    }
}
