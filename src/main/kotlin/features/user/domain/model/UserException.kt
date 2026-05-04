package com.haykor.features.user.domain.model

import com.haykor.core.exception.ConflictException
import com.haykor.core.exception.NotFoundException

object UserException {
    class UserNotFound : NotFoundException("User not found")

    class UserAlreadyExists : ConflictException("User already exists")
}
