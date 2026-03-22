package com.haykor.di

import com.haykor.features.user.data.BCryptPasswordHasher
import com.haykor.features.user.data.UserRepositoryImpl
import com.haykor.features.user.data.UserSocialsRepositoryImpl
import com.haykor.features.user.domain.*
import org.koin.core.module.dsl.new
import org.koin.dsl.module

val userModule = module {
    single<UserRepository> { UserRepositoryImpl(get()) }
    single<UserSocialsRepository> { UserSocialsRepositoryImpl(get()) }
    single<PasswordHasher> { BCryptPasswordHasher() }

    factory { new(::CreateUserUseCase) }
    factory { new(::GetUserUseCase) }
}