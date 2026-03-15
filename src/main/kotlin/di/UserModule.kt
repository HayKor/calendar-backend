package com.haykor.di

import com.haykor.features.user.data.BCryptPasswordHasher
import com.haykor.features.user.data.UserRepositoryImpl
import com.haykor.features.user.domain.CreateUserUseCase
import com.haykor.features.user.domain.GetUserUseCase
import com.haykor.features.user.domain.PasswordHasher
import com.haykor.features.user.domain.UserRepository
import org.koin.dsl.module

val userModule = module {
    single<UserRepository> { UserRepositoryImpl(get()) }
    single<PasswordHasher> { BCryptPasswordHasher() }

    factory { CreateUserUseCase(get(), get()) }
    factory { GetUserUseCase(get()) }
}