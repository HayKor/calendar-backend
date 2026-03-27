package com.haykor.features.user.presentation

import kotlinx.serialization.Serializable

@Serializable
data class UserCreateRequest(
    val name: String,
    val email: String,
    val password: String,
)

@Serializable
data class UserResponse(
    val id: Int,
    val name: String,
    val email: String,
)
