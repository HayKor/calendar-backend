package com.haykor.core.exception

import io.ktor.http.*

abstract class AppException(
    override val message: String,
    val statusCode: HttpStatusCode
) : RuntimeException(message)
