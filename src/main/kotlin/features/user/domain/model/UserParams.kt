package com.haykor.features.user.domain.model

data class CreateUserParams(
    val name: String,
    val email: String,
    val password: String,
    val isVerified: Boolean,
)

data class CreateUserDbParams(
    val name: String,
    val email: String,
    val hashedPassword: String? = null,
    val isVerified: Boolean,
)