package features.event.domain.service

import com.haykor.features.event.data.service.RRuleExpanderImpl
import com.haykor.features.event.domain.service.RRuleExpander
import features.event.domain.model.EventExceptionFixtures
import features.event.domain.model.EventFixtures
import kotlinx.datetime.LocalDate
import org.junit.Test
import java.time.OffsetDateTime
import java.time.ZoneOffset
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RRuleExpanderTest {

    private val expander: RRuleExpander = RRuleExpanderImpl()

    // April 6, 2026, is a Monday — convenient anchor for weekly tests
    private val monday = OffsetDateTime.of(2026, 4, 6, 10, 0, 0, 0, ZoneOffset.UTC)
    private val farFuture = monday.plusYears(2)

    // -------------------------------------------------------------------------
    // null rrule
    // -------------------------------------------------------------------------

    @Test
    fun `returns empty list when rrule is null`() {
        val event = EventFixtures.event(rrule = null, isRecurring = false)
        val result = expander.expand(event, monday, farFuture, emptyList())
        assertTrue(result.isEmpty())
    }

    // -------------------------------------------------------------------------
    // FREQ=DAILY
    // -------------------------------------------------------------------------

    @Test
    fun `DAILY generates one occurrence per day in range`() {
        val event = EventFixtures.event(rrule = "FREQ=DAILY", startAt = monday)
        val to = monday.plusDays(4)

        val result = expander.expand(event, monday, to, emptyList())

        assertEquals(5, result.size) // day 0..4 inclusive
        assertEquals(LocalDate(2026, 4, 6), result[0].originalDate)
        assertEquals(LocalDate(2026, 4, 10), result[4].originalDate)
    }

    @Test
    fun `DAILY with INTERVAL=2 skips every other day`() {
        val event = EventFixtures.event(rrule = "FREQ=DAILY;INTERVAL=2", startAt = monday)
        val to = monday.plusDays(6)

        val result = expander.expand(event, monday, to, emptyList())

        assertEquals(4, result.size) // days 0, 2, 4, 6
        assertEquals(LocalDate(2026, 4, 6), result[0].originalDate)
        assertEquals(LocalDate(2026, 4, 8), result[1].originalDate)
        assertEquals(LocalDate(2026, 4, 10), result[2].originalDate)
        assertEquals(LocalDate(2026, 4, 12), result[3].originalDate)
    }

    @Test
    fun `DAILY with COUNT=3 returns exactly 3 occurrences`() {
        val event = EventFixtures.event(rrule = "FREQ=DAILY;COUNT=3", startAt = monday)

        val result = expander.expand(event, monday, farFuture, emptyList())

        assertEquals(3, result.size)
    }

    @Test
    fun `DAILY with UNTIL stops at given date inclusive`() {
        val event = EventFixtures.event(rrule = "FREQ=DAILY;UNTIL=20260409T235959Z", startAt = monday)

        val result = expander.expand(event, monday, farFuture, emptyList())

        assertEquals(4, result.size) // Apr 6, 7, 8, 9
        assertEquals(LocalDate(2026, 4, 9), result.last().originalDate)
    }

    @Test
    fun `DAILY occurrences before from are excluded`() {
        val event = EventFixtures.event(rrule = "FREQ=DAILY", startAt = monday)
        val from = monday.plusDays(2)
        val to = monday.plusDays(4)

        val result = expander.expand(event, from, to, emptyList())

        assertEquals(3, result.size) // days 2, 3, 4 only
        assertEquals(LocalDate(2026, 4, 8), result[0].originalDate)
    }

    // -------------------------------------------------------------------------
    // FREQ=WEEKLY
    // -------------------------------------------------------------------------

    @Test
    fun `WEEKLY generates occurrence every 7 days`() {
        val event = EventFixtures.event(rrule = "FREQ=WEEKLY", startAt = monday)
        val to = monday.plusWeeks(3)

        val result = expander.expand(event, monday, to, emptyList())

        assertEquals(4, result.size) // weeks 0, 1, 2, 3
        assertEquals(LocalDate(2026, 4, 6), result[0].originalDate)
        assertEquals(LocalDate(2026, 4, 13), result[1].originalDate)
        assertEquals(LocalDate(2026, 4, 20), result[2].originalDate)
        assertEquals(LocalDate(2026, 4, 27), result[3].originalDate)
    }

    @Test
    fun `WEEKLY with BYDAY generates only specified weekdays`() {
        val event = EventFixtures.event(rrule = "FREQ=WEEKLY;BYDAY=MO,WE,FR", startAt = monday)
        val to = monday.plusDays(7)

        val result = expander.expand(event, monday, to, emptyList())

        // Mon Apr 6, Wed Apr 8, Fri Apr 10, Mon Apr 13
        assertEquals(4, result.size)
        assertEquals(LocalDate(2026, 4, 6), result[0].originalDate) // Monday
        assertEquals(LocalDate(2026, 4, 8), result[1].originalDate) // Wednesday
        assertEquals(LocalDate(2026, 4, 10), result[2].originalDate) // Friday
        assertEquals(LocalDate(2026, 4, 13), result[3].originalDate) // Monday
    }

    @Test
    fun `WEEKLY with INTERVAL=2 generates biweekly occurrences`() {
        val event = EventFixtures.event(rrule = "FREQ=WEEKLY;INTERVAL=2", startAt = monday)
        val to = monday.plusWeeks(4)

        val result = expander.expand(event, monday, to, emptyList())

        assertEquals(3, result.size) // weeks 0, 2, 4
        assertEquals(LocalDate(2026, 4, 6), result[0].originalDate)
        assertEquals(LocalDate(2026, 4, 20), result[1].originalDate)
        assertEquals(LocalDate(2026, 5, 4), result[2].originalDate)
    }

    @Test
    fun `WEEKLY with COUNT=2 returns exactly 2 occurrences`() {
        val event = EventFixtures.event(rrule = "FREQ=WEEKLY;BYDAY=MO,WE,FR;COUNT=2", startAt = monday)

        val result = expander.expand(event, monday, farFuture, emptyList())

        assertEquals(2, result.size)
    }

    // -------------------------------------------------------------------------
    // FREQ=MONTHLY
    // -------------------------------------------------------------------------

    @Test
    fun `MONTHLY generates occurrence each month on same day`() {
        val event = EventFixtures.event(rrule = "FREQ=MONTHLY", startAt = monday)
        val to = monday.plusMonths(3)

        val result = expander.expand(event, monday, to, emptyList())

        assertEquals(4, result.size)
        assertEquals(LocalDate(2026, 4, 6), result[0].originalDate)
        assertEquals(LocalDate(2026, 5, 6), result[1].originalDate)
        assertEquals(LocalDate(2026, 6, 6), result[2].originalDate)
        assertEquals(LocalDate(2026, 7, 6), result[3].originalDate)
    }

    @Test
    fun `MONTHLY with COUNT=2 returns exactly 2 occurrences`() {
        val event = EventFixtures.event(rrule = "FREQ=MONTHLY;COUNT=2", startAt = monday)

        val result = expander.expand(event, monday, farFuture, emptyList())

        assertEquals(2, result.size)
    }

    // -------------------------------------------------------------------------
    // FREQ=YEARLY
    // -------------------------------------------------------------------------

    @Test
    fun `YEARLY generates occurrence each year on same date`() {
        val event = EventFixtures.event(rrule = "FREQ=YEARLY", startAt = monday)
        val to = monday.plusYears(2)

        val result = expander.expand(event, monday, to, emptyList())

        assertEquals(3, result.size)
        assertEquals(LocalDate(2026, 4, 6), result[0].originalDate)
        assertEquals(LocalDate(2027, 4, 6), result[1].originalDate)
        assertEquals(LocalDate(2028, 4, 6), result[2].originalDate)
    }

    // -------------------------------------------------------------------------
    // Occurrence fields
    // -------------------------------------------------------------------------

    @Test
    fun `occurrence startAt preserves original time of day`() {
        val event = EventFixtures.event(rrule = "FREQ=DAILY", startAt = monday)
        val to = monday.plusDays(1)

        val result = expander.expand(event, monday, to, emptyList())

        // time should stay 10:00 UTC on each day
        result.forEach { assertEquals(10, it.startAt.hour) }
    }

    @Test
    fun `occurrence endAt preserves original duration`() {
        val event = EventFixtures.event(
            rrule = "FREQ=DAILY",
            startAt = monday,
            endAt = monday.plusHours(2),
        )
        val to = monday.plusDays(1)

        val result = expander.expand(event, monday, to, emptyList())

        result.forEach { occurrence ->
            val duration = java.time.Duration.between(occurrence.startAt, occurrence.endAt)
            assertEquals(2, duration.toHours())
        }
    }

    // -------------------------------------------------------------------------
    // Exceptions — cancellations
    // -------------------------------------------------------------------------

    @Test
    fun `cancelled occurrence has isCancelled=true`() {
        val event = EventFixtures.event(rrule = "FREQ=DAILY", startAt = monday)
        val cancelledDate = LocalDate(2026, 4, 7) // Tuesday
        val exception = EventExceptionFixtures.exception(
            eventId = event.id,
            originalDate = cancelledDate,
            isDeleted = true,
        )
        val to = monday.plusDays(2)

        val result = expander.expand(event, monday, to, listOf(exception))

        val cancelled = result.find { it.originalDate == cancelledDate }
        assertTrue(cancelled!!.isCancelled)
        assertFalse(result.first { it.originalDate != cancelledDate }.isCancelled)
    }

    @Test
    fun `non-cancelled occurrences are not affected by other exceptions`() {
        val event = EventFixtures.event(rrule = "FREQ=DAILY", startAt = monday)
        val exception = EventExceptionFixtures.exception(
            eventId = event.id,
            originalDate = LocalDate(2026, 4, 7),
            isDeleted = true,
        )
        val to = monday.plusDays(2)

        val result = expander.expand(event, monday, to, listOf(exception))

        val unaffected = result.filter { !it.isCancelled }
        assertEquals(2, unaffected.size)
    }

    // -------------------------------------------------------------------------
    // Exceptions — field overrides
    // -------------------------------------------------------------------------

    @Test
    fun `title override replaces original title`() {
        val event = EventFixtures.event(rrule = "FREQ=DAILY", startAt = monday, title = "Original")
        val overriddenDate = LocalDate(2026, 4, 7)
        val exception = EventExceptionFixtures.exception(
            eventId = event.id,
            originalDate = overriddenDate,
            titleOverride = "Override",
        )

        val result = expander.expand(event, monday, monday.plusDays(2), listOf(exception))

        assertEquals("Override", result.find { it.originalDate == overriddenDate }!!.title)
        assertEquals("Original", result.find { it.originalDate != overriddenDate }!!.title)
    }

    @Test
    fun `startAt override replaces original startAt`() {
        val event = EventFixtures.event(rrule = "FREQ=DAILY", startAt = monday)
        val overriddenDate = LocalDate(2026, 4, 7)
        val newStart = monday.plusDays(1).withHour(15) // moved to 15:00
        val exception = EventExceptionFixtures.exception(
            eventId = event.id,
            originalDate = overriddenDate,
            startAtOverride = newStart,
        )

        val result = expander.expand(event, monday, monday.plusDays(2), listOf(exception))

        assertEquals(newStart, result.find { it.originalDate == overriddenDate }!!.startAt)
    }

    @Test
    fun `location override replaces original location`() {
        val event = EventFixtures.event(rrule = "FREQ=DAILY", startAt = monday, location = "Berlin")
        val overriddenDate = LocalDate(2026, 4, 7)
        val exception = EventExceptionFixtures.exception(
            eventId = event.id,
            originalDate = overriddenDate,
            locationOverride = "Paris",
        )

        val result = expander.expand(event, monday, monday.plusDays(2), listOf(exception))

        assertEquals("Paris", result.find { it.originalDate == overriddenDate }!!.location)
        assertEquals("Berlin", result.find { it.originalDate != overriddenDate }!!.location)
    }

    @Test
    fun `exception on a date outside range does not affect results`() {
        val event = EventFixtures.event(rrule = "FREQ=DAILY", startAt = monday)
        val exception = EventExceptionFixtures.exception(
            eventId = event.id,
            originalDate = LocalDate(2026, 5, 1), // outside range
            isDeleted = true,
        )
        val to = monday.plusDays(2)

        val result = expander.expand(event, monday, to, listOf(exception))

        assertTrue(result.none { it.isCancelled })
    }
}
