package com.haykor.features.event.domain.model

import com.haykor.core.exception.BadRequestException
import com.haykor.core.exception.ConflictException
import com.haykor.core.exception.ForbiddenException
import com.haykor.core.exception.NotFoundException

object EventError {
    class NotFound : NotFoundException("Event not found")
    class Forbidden : ForbiddenException("You don't have access to this event")
    class MissingFreq : BadRequestException("Frequency not provided on recurrent event")
    class NotRecurring : ConflictException("Event is not recurring")
}
