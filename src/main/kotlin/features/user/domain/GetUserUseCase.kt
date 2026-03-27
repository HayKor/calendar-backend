package com.haykor.features.user.domain

class GetUserUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(id: Int): User {
        return repository.findById(id) ?: throw UserException.UserNotFound()
    }
}
