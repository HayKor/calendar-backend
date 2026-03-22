package com.haykor.features.user.presentation

import com.haykor.core.exception.NotFoundException

class UserNotFound(message: String = "User not found") : NotFoundException(message)