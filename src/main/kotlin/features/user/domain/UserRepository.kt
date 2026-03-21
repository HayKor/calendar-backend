package com.haykor.features.user.domain

interface UserRepository {
    suspend fun create(user: CreateUserParams): User
    suspend fun findByEmail(email: String): User?
    suspend fun findById(id: Int): User?
}