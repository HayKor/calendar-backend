package com.haykor.features.user.domain

import com.haykor.core.exception.ConflictException
import com.haykor.core.exception.NotFoundException

object UserException {
    class UserNotFound() : NotFoundException("User not found")
    class UserAlreadyExists() : ConflictException(message = "User already exists")
}