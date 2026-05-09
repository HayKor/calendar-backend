package com.haykor.features.event.domain.model

import com.haykor.core.exception.ForbiddenException
import com.haykor.core.exception.NotFoundException

object EventException {
    class NotFound : NotFoundException("Event not found")
    class Forbidden : ForbiddenException("You don't have access to this event")
}
