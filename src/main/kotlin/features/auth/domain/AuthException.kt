package com.haykor.features.auth.domain

sealed class AuthException(message: String) : Exception(message) {
    class InvalidCredentials : AuthException("Invalid email or password")
    class UserNotFound : AuthException("No user found with this email")
}