package com.haykor.core.exception

import io.ktor.http.*

abstract class NotFoundException(
    message: String,
) : AppException(message, HttpStatusCode.NotFound)

abstract class UnauthorizedException(
    message: String,
) : AppException(message, HttpStatusCode.Unauthorized)

abstract class ConflictException(
    message: String,
) : AppException(message, HttpStatusCode.Conflict)
