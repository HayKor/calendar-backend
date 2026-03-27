@file:OptIn(ExperimentalUuidApi::class)

package com.haykor.features.auth.data

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import java.util.*
import kotlin.uuid.ExperimentalUuidApi

class JwtEncryptor(
    secret: String,
    val accessTokenLifetime: Long,
    val refreshTokenLifetime: Long,
    private val issuer: String,
    private val audience: String,
) {
    private val algorithm = Algorithm.HMAC256(secret)

    val verifier: JWTVerifier =
        JWT
            .require(algorithm)
            .withAudience(audience)
            .withIssuer(issuer)
            .build()

    fun encryptAccessToken(userId: Int): String = JWT
        .create()
        .withAudience(audience)
        .withIssuer(issuer)
        .withSubject(userId.toString())
        .withExpiresAt(Date(System.currentTimeMillis() + accessTokenLifetime))
        .sign(algorithm)
}
