package com.haykor.features.user.domain

import com.haykor.features.user.presentation.UserCreateRequest

class CreateUserUseCase(
    private val repository: UserRepository,
    private val passwordHasher: PasswordHasher
) {
    suspend fun execute(request: UserCreateRequest): Int {
        if (repository.findByEmail(request.email) != null) {
            throw IllegalArgumentException("User already exists")
        }

        val newUser = User(
            name = request.name,
            email = request.email,
            hashedPassword = passwordHasher.hash(request.password)
        )

        return repository.create(newUser)
    }
}
