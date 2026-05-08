package common

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

val testJwtAlgorithm: Algorithm = Algorithm.HMAC256("test-secret")
val verifier: JWTVerifier = JWT.require(testJwtAlgorithm).build()
fun generateTestToken(userId: Int): String = JWT.create()
    .withSubject(userId.toString())
    .withExpiresAt(Date(System.currentTimeMillis() + 60_000))
    .sign(testJwtAlgorithm)
