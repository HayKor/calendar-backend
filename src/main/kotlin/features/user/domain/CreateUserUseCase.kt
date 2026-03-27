package com.haykor.features.user.domain

import com.haykor.features.user.presentation.UserCreateRequest

class CreateUserUseCase(
    private val repository: UserRepository,
    private val passwordHasher: PasswordHasher,
) {
    suspend operator fun invoke(request: UserCreateRequest): User {
        if (repository.findByEmail(request.email) != null) {
            throw UserException.UserAlreadyExists()
        }

        val newUser =
            CreateUserParams(
                name = request.name,
                email = request.email,
                hashedPassword = passwordHasher.hash(request.password),
                isVerified = false,
            )

        return repository.create(newUser)
    }
}
