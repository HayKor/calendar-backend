package com.haykor.features.user.domain.usecase

import com.haykor.features.user.domain.model.User
import com.haykor.features.user.domain.model.UserException
import com.haykor.features.user.domain.repository.UserRepository

class GetUserUseCase(
    private val repository: UserRepository,
) {
    suspend operator fun invoke(id: Int): User = repository.findById(id) ?: throw UserException.UserNotFound()
}
