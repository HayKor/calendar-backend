package com.haykor.core.di

import com.haykor.features.user.data.repository.UserRepositoryImpl
import com.haykor.features.user.data.repository.UserSocialsRepositoryImpl
import com.haykor.features.user.data.service.BCryptPasswordHasher
import com.haykor.features.user.domain.*
import com.haykor.features.user.domain.repository.UserRepository
import com.haykor.features.user.domain.repository.UserSocialsRepository
import com.haykor.features.user.domain.service.PasswordHasher
import com.haykor.features.user.domain.usecase.CreateUserUseCase
import com.haykor.features.user.domain.usecase.GetUserUseCase
import org.koin.core.module.dsl.new
import org.koin.dsl.module

val userModule =
    module {
        single<UserRepository> { UserRepositoryImpl(get()) }
        single<UserSocialsRepository> { UserSocialsRepositoryImpl(get()) }
        single<PasswordHasher> { BCryptPasswordHasher() }

        single { new(::CreateUserUseCase) }
        single { new(::GetUserUseCase) }
    }
