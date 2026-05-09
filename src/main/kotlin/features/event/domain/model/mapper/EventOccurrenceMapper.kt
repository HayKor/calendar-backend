package com.haykor.features.event.domain.model.mapper

import com.haykor.core.util.mapper.toKotlinLocalDate
import com.haykor.features.event.domain.model.Event
import com.haykor.features.event.domain.model.EventException
import com.haykor.features.event.domain.model.EventOccurrence
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number
import java.time.Duration

fun Event.toOccurrence(
    date: LocalDate? = null,
    exception: EventException? = null,
): EventOccurrence {
    val originalDate = date ?: startAt.toKotlinLocalDate()
    val occurrenceStart = exception?.startAtOverride
        ?: startAt
            .withYear(originalDate.year)
            .withMonth(originalDate.month.number)
            .withDayOfMonth(originalDate.day)

    val occurrenceEnd = exception?.endAtOverride
        ?: endAt?.let { end ->
            occurrenceStart.plus(Duration.between(startAt, end))
        }

    return EventOccurrence(
        eventId = id,
        originalDate = originalDate,
        title = exception?.titleOverride ?: title,
        description = description,
        location = exception?.locationOverride ?: location,
        startAt = occurrenceStart,
        endAt = occurrenceEnd,
        isAllDay = isAllDay,
        eventTimezone = eventTimezone,
        visibility = visibility,
        categoryId = categoryId,
        isRecurring = isRecurring,
        isCancelled = exception?.isDeleted ?: false,
    )
}
