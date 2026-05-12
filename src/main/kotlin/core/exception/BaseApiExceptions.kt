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

abstract class ForbiddenException(
    message: String,
) : AppException(message, HttpStatusCode.Forbidden)

abstract class BadRequestException(
    message: String,
) : AppException(message, HttpStatusCode.BadRequest)
