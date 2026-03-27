package com.haykor.features.user.domain

import com.haykor.core.exception.ConflictException
import com.haykor.core.exception.NotFoundException

class UserNotFound(message: String = "User not found") : NotFoundException(message)
class UserAlreadyExists() : ConflictException(message = "User already exists")