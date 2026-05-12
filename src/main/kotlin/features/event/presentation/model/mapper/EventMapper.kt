package com.haykor.features.event.presentation.model.mapper

import com.haykor.features.event.domain.model.*
import com.haykor.features.event.domain.repository.CreateEventParams
import com.haykor.features.event.domain.repository.UpdateEventParams
import com.haykor.features.event.presentation.model.*

// features/event/presentation/model/mapper/EventMapper.kt
fun Event.toResponse() = EventResponse(
    id = id, userId = userId, categoryId = categoryId,
    title = title, description = description, location = location,
    startAt = startAt, endAt = endAt, isAllDay = isAllDay,
    eventTimezone = eventTimezone, visibility = visibility,
    isRecurring = isRecurring, rrule = rrule,
    createdAt = createdAt, updatedAt = updatedAt,
)

fun EventOccurrence.toResponse() = EventOccurrenceResponse(
    eventId = eventId, originalDate = originalDate,
    title = title, description = description, location = location,
    startAt = startAt, endAt = endAt, isAllDay = isAllDay,
    eventTimezone = eventTimezone, visibility = visibility,
    categoryId = categoryId, isRecurring = isRecurring, isCancelled = isCancelled,
)

fun EventException.toResponse() = EventExceptionResponse(
    id = id,
    eventId = eventId,
    originalDate = originalDate,
    isDeleted = isDeleted,
    titleOverride = titleOverride,
    startAtOverride = startAtOverride,
    endAtOverride = endAtOverride,
    locationOverride = locationOverride,
)

fun CreateEventRequest.toParams(userId: Int, rrule: RRuleInput) = CreateEventParams(
    userId = userId, categoryId = categoryId, title = title,
    description = description, location = location,
    startAt = startAt, endAt = endAt, isAllDay = isAllDay,
    eventTimezone = eventTimezone, visibility = visibility, rrule = rrule, isRecurring = rrule.isRecurring,
)

fun UpdateEventRequest.toParams(rrule: RRuleInput) = UpdateEventParams(
    categoryId = categoryId,
    title = title,
    description = description,
    location = location,
    startAt = startAt,
    endAt = endAt,
    isAllDay = isAllDay,
    eventTimezone = eventTimezone,
    visibility = visibility,
    rrule = rrule,
    isRecurring = rrule.isRecurring,
)

fun UpdateOccurrenceRequest.toParams() = UpdateOccurrenceParams(
    title = title,
    startAt = startAt,
    endAt = endAt,
    location = location,
)
