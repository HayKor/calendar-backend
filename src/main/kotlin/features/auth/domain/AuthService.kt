package com.haykor.features.auth.domain

interface AuthService {
    suspend fun createTokenPair(email: String, password: String, userIp: String, userAgent: String): Auth
}