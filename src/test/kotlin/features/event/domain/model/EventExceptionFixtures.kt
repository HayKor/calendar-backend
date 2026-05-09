package features.event.domain.model

import com.haykor.core.util.mapper.toKotlinLocalDate
import com.haykor.features.event.domain.model.EventException
import kotlinx.datetime.LocalDate
import java.time.OffsetDateTime

object EventExceptionFixtures {
    private val currentDate = OffsetDateTime.now()

    fun exception(
        id: Int = 1,
        eventId: Int = 1,
        originalDate: LocalDate = currentDate.toKotlinLocalDate(),
        isDeleted: Boolean = false,
        titleOverride: String? = null,
        startAtOverride: OffsetDateTime? = null,
        endAtOverride: OffsetDateTime? = null,
        locationOverride: String? = null,
        createdAt: OffsetDateTime = currentDate,
    ) = EventException(
        id = id,
        eventId = eventId,
        originalDate = originalDate,
        isDeleted = isDeleted,
        titleOverride = titleOverride,
        startAtOverride = startAtOverride,
        endAtOverride = endAtOverride,
        locationOverride = locationOverride,
        createdAt = createdAt,
    )
}
