@file:OptIn(ExperimentalUuidApi::class)

package com.haykor.features.auth.data

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import java.util.*
import kotlin.uuid.ExperimentalUuidApi

class JwtEncryptor(
    secret: String,
    private val issuer: String,
    private val audience: String
) {
    private val algorithm = Algorithm.HMAC256(secret)

    val accessTokenLifetime = 30L * 60L * 1000L // 30 mins
    val refreshTokenLifetime = 30L * 24L * 60L * 60L * 1000L // 30 days // TODO: change to env

    val verifier: JWTVerifier = JWT.require(algorithm)
        .withAudience(audience)
        .withIssuer(issuer)
        .build()

    fun encryptAccessToken(userId: Int): String =
        JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withSubject(userId.toString())
            .withExpiresAt(Date(System.currentTimeMillis() + accessTokenLifetime))
            .sign(algorithm)
}