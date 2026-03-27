package com.haykor.features.auth.domain

import com.haykor.core.exception.UnauthorizedException

object AuthException {
    class InvalidToken : UnauthorizedException("Invalid token or session doesn't exist")

    class InvalidCredentials : UnauthorizedException("Invalid credentials")
}
