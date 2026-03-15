package com.haykor.features.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.Date

class AuthService(
    private val database: Database,
    private val secret: String,
    private val issuer: String,
    private val audience: String
) {
    private val accessTokenLifetime = 30L * 60L * 1000L // 30 mins
    private val refreshTokenLifetime = 30L * 24L * 60L * 60L * 1000L // 30 days // TODO: change to env

    fun generateRefreshToken(userId: Int, username: String): String {
        val refreshToken = createToken(userId, username, refreshTokenLifetime)
        val accessToken = createToken(userId, username, accessTokenLifetime)

        transaction(database) {
            AuthSessionTable.insert {
                it[user] = userId
                it[userIp] = "" // TODO: save IP
                it[this.accessToken] = accessToken
                it[this.refreshToken] = refreshToken
            }
        }
        return refreshToken
    }

    private fun createToken(userId: Int, username: String, lifetime: Long): String =
        JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("userId", userId)
            .withClaim("username", username)
            .withExpiresAt(Date(System.currentTimeMillis() + lifetime))
            .sign(Algorithm.HMAC256(secret))
}
