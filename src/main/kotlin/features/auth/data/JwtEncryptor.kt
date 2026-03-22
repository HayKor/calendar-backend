@file:OptIn(ExperimentalUuidApi::class)

package com.haykor.features.auth.data

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import java.util.*
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class JwtEncryptor(
    secret: String,
    private val issuer: String,
    private val audience: String
) {
    private val algorithm = Algorithm.HMAC256(secret)

    val verifier: JWTVerifier = JWT.require(algorithm)
        .withAudience(audience)
        .withIssuer(issuer)
        .build()

    fun encryptToken(token: Uuid, lifetime: Long): String =
        JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("token", token.toString())
            .withExpiresAt(Date(System.currentTimeMillis() + lifetime))
            .sign(algorithm)
}