package com.haykor.features.user.domain.usecase

import com.haykor.features.user.domain.model.CreateUserParams
import com.haykor.features.user.domain.model.User
import com.haykor.features.user.domain.model.UserException
import com.haykor.features.user.domain.repository.UserRepository
import com.haykor.features.user.domain.service.PasswordHasher
import com.haykor.features.user.presentation.model.UserCreateRequest

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
