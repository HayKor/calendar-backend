package com.haykor.core.exception

import io.ktor.http.*

class BadRequest(message: String) : AppException(message, HttpStatusCode.BadRequest)
class Forbidden(message: String = "You do not have permission") : AppException(message, HttpStatusCode.Forbidden)
