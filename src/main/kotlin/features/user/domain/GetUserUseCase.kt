package com.haykor.features.user.domain

import com.haykor.features.user.presentation.UserResponse

class GetUserUseCase(
    private val repository: UserRepository
) {
    suspend fun execute(id: Int): UserResponse? {
        val user = repository.findById(id) ?: return null
        return UserResponse(id, user.name, user.email)
    }
}
