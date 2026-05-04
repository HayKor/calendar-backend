package com.haykor.features.user.domain.service

interface PasswordHasher {
    fun hash(password: String): String

    fun check(
        password: String,
        hashed: String,
    ): Boolean
}