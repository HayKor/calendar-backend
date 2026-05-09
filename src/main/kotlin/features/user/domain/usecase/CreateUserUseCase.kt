package com.haykor.features.user.domain.usecase

import com.haykor.features.user.domain.model.User
import com.haykor.features.user.domain.model.UserException
import com.haykor.features.user.domain.repository.CreateUserDbParams
import com.haykor.features.user.domain.repository.CreateUserParams
import com.haykor.features.user.domain.repository.UserRepository
import com.haykor.features.user.domain.service.PasswordHasher

class CreateUserUseCase(
    private val repository: UserRepository,
    private val passwordHasher: PasswordHasher,
) {
    suspend operator fun invoke(params: CreateUserParams): User {
        if (repository.findByEmail(params.email) != null) {
            throw UserException.UserAlreadyExists()
        }

        val newUser =
            CreateUserDbParams(
                name = params.name,
                email = params.email,
                hashedPassword = passwordHasher.hash(params.password),
                isVerified = false,
            )

        return repository.create(newUser)
    }
}
