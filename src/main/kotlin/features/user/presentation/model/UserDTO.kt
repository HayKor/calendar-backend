package com.haykor.features.user.presentation.model

import com.haykor.features.user.domain.model.User
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

fun User.toUserResponse() = UserResponse(
    id = id,
    name = name,
    email = email,
)
