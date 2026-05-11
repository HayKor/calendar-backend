package features.event.domain.model

import com.haykor.core.util.mapper.toKotlinLocalDate
import com.haykor.core.visibility.domain.model.Visibility
import com.haykor.features.event.domain.model.Event
import com.haykor.features.event.domain.model.EventOccurrence
import com.haykor.features.event.domain.repository.CreateEventParams
import kotlinx.datetime.LocalDate
import java.time.OffsetDateTime

internal val currentDate = OffsetDateTime.now()

object EventFixtures {
    fun createParams(
        userId: Int = 1,
        categoryId: Int? = 1,
        title: String = "Help my mom",
        description: String = "Help my mom 2night",
        location: String = "New York",
        startAt: OffsetDateTime = currentDate,
        endAt: OffsetDateTime = currentDate.plusHours(1),
        isAllDay: Boolean = false,
        eventTimezone: String = "Europe/Berlin",
        visibility: Visibility = Visibility.Friends,
        isRecurring: Boolean = true,
//        rrule: String? = "FREQ=DAILY;UNTIL=20260612T235959Z", // daily till 12.06.2026 23:59 UTC+0
        rrule: String? = "FREQ=DAILY", // daily
        recurrenceUntil: OffsetDateTime = currentDate.plusMonths(1),
    ) = CreateEventParams(
        userId = userId,
        categoryId = categoryId,
        title = title,
        description = description,
        location = location,
        startAt = startAt,
        endAt = endAt,
        isAllDay = isAllDay,
        eventTimezone = eventTimezone,
        visibility = visibility,
        isRecurring = isRecurring,
        rrule = rrule,
        recurrenceUntil = recurrenceUntil,
    )

    fun event(
        id: Int = 1,
        userId: Int = 1,
        categoryId: Int? = 1,
        title: String = "Help my mom",
        description: String = "Help my mom 2night",
        location: String = "New York",
        startAt: OffsetDateTime = currentDate,
        endAt: OffsetDateTime = currentDate.plusHours(1),
        isAllDay: Boolean = false,
        eventTimezone: String = "Europe/Berlin",
        visibility: Visibility = Visibility.Friends,
        isRecurring: Boolean = true,
//        rrule: String? = "FREQ=DAILY;UNTIL=20260612T235959Z", // daily till 12.06.2026 23:59 UTC+0
        rrule: String? = "FREQ=DAILY", // daily
        recurrenceUntil: OffsetDateTime = currentDate.plusMonths(1),
        createdAt: OffsetDateTime = currentDate.minusDays(2),
        updatedAt: OffsetDateTime = createdAt,
    ) = Event(
        id = id,
        userId = userId,
        categoryId = categoryId,
        title = title,
        description = description,
        location = location,
        eventTimezone = eventTimezone,
        startAt = startAt,
        endAt = endAt,
        isAllDay = isAllDay,
        visibility = visibility,
        isRecurring = isRecurring,
        rrule = rrule,
        recurrenceUntil = recurrenceUntil,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

    fun occurrence(
        eventId: Int = 1,
        categoryId: Int? = 1,
        title: String = "Help my mom",
        description: String = "Help my mom 2night",
        location: String = "New York",
        startAt: OffsetDateTime = currentDate.plusDays(1),
        endAt: OffsetDateTime = currentDate.plusHours(1),
        originalDate: LocalDate = startAt.toKotlinLocalDate(), // same as startAt
        isAllDay: Boolean = false,
        eventTimezone: String = "Europe/Berlin",
        visibility: Visibility = Visibility.Friends,
        isRecurring: Boolean = true,
        isCanceled: Boolean = false,
    ) = EventOccurrence(
        eventId = eventId,
        originalDate = originalDate,
        categoryId = categoryId,
        title = title,
        description = description,
        location = location,
        eventTimezone = eventTimezone,
        startAt = startAt,
        endAt = endAt,
        isAllDay = isAllDay,
        visibility = visibility,
        isRecurring = isRecurring,
        isCancelled = isCanceled,
    )
}
