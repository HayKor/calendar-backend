package com.haykor.features.user.domain.model

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val hashedPassword: String? = null,
    val isVerified: Boolean? = null,
)
