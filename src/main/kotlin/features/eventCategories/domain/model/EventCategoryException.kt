package com.haykor.features.eventCategories.domain.model

import com.haykor.core.exception.ForbiddenException
import com.haykor.core.exception.NotFoundException

object EventCategoryException {
    class NotFound : NotFoundException("Event category not found")
    class Forbidden : ForbiddenException("You don't have access to this category")
}
