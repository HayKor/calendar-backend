package com.haykor.features.user.data.service

import com.haykor.features.user.domain.service.PasswordHasher
import org.mindrot.jbcrypt.BCrypt

class BCryptPasswordHasher : PasswordHasher {
    override fun hash(password: String): String = BCrypt.hashpw(password, BCrypt.gensalt(12))

    override fun check(
        password: String,
        hashed: String,
    ): Boolean = try {
        BCrypt.checkpw(password, hashed)
    } catch (e: Exception) {
        false
    }
}