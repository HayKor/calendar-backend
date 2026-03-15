package com.haykor.features.user.domain

data class User(
    val id: Int? = null,
    val name: String,
    val email: String,
    val hashedPassword: String
)