package features.event.domain.usecase

import com.haykor.core.visibility.domain.model.ViewerRelation
import com.haykor.core.visibility.domain.model.Visibility
import com.haykor.core.visibility.domain.usecase.ResolveViewerRelationUseCase
import com.haykor.features.event.domain.repository.EventExceptionRepository
import com.haykor.features.event.domain.repository.EventRepository
import com.haykor.features.event.domain.service.RRuleExpander
import com.haykor.features.event.domain.usecase.GetEventsInRangeUseCase
import com.haykor.features.eventCategories.domain.repository.EventCategoriesRepository
import features.event.domain.model.EventExceptionFixtures
import features.event.domain.model.EventFixtures
import features.event.domain.model.currentDate
import features.eventCategories.domain.model.EventCategoryFixtures
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class GetEventsInRangeUseCaseTest {

    private val eventRepository = mockk<EventRepository>()
    private val eventExceptionRepository = mockk<EventExceptionRepository>()
    private val eventCategoriesRepository = mockk<EventCategoriesRepository>()
    private val resolveViewerRelation = mockk<ResolveViewerRelationUseCase>()
    private val rruleExpander = mockk<RRuleExpander>()
    private val useCase = GetEventsInRangeUseCase(
        eventRepository,
        eventExceptionRepository,
        eventCategoriesRepository,
        resolveViewerRelation,
        rruleExpander,
    )

    private val from = currentDate.minusDays(1)
    private val to = from.plusMonths(1)

    // default mocks reused across most tests
    private fun mockDefaults(targetUserId: Int = 1, relation: ViewerRelation = ViewerRelation.Owner) {
        coEvery { resolveViewerRelation(any(), targetUserId) } returns relation
        coEvery { eventCategoriesRepository.getAllByUser(targetUserId) } returns emptyList()
    }

    // --- Happy path ---

    @Test
    fun `returns empty list when no events`() = runTest {
        mockDefaults(targetUserId = 10)
        coEvery { eventRepository.getByUserIdAndRange(10, from, to) } returns emptyList()
        coEvery { eventExceptionRepository.getByEventIds(emptyList(), from, to) } returns emptyList()

        val result = useCase(requesterId = 1, targetUserId = 10, from, to)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `returns regular events directly without expanding`() = runTest {
        mockDefaults()
        val event = EventFixtures.event(isRecurring = false, rrule = null)
        coEvery { eventRepository.getByUserIdAndRange(1, from, to) } returns listOf(event)
        coEvery { eventExceptionRepository.getByEventIds(emptyList(), from, to) } returns emptyList()

        val result = useCase(requesterId = 1, targetUserId = 1, from, to)

        assertEquals(1, result.size)
        assertEquals(event.title, result.first().title)
        coVerify(exactly = 0) { rruleExpander.expand(any(), any(), any(), any()) }
    }

    @Test
    fun `returns recurring events expanded via rruleExpander`() = runTest {
        mockDefaults()
        val event = EventFixtures.event(isRecurring = true)
        val occurrence = EventFixtures.occurrence(eventId = event.id)
        coEvery { eventRepository.getByUserIdAndRange(1, from, to) } returns listOf(event)
        coEvery { eventExceptionRepository.getByEventIds(listOf(event.id), from, to) } returns emptyList()
        coEvery { rruleExpander.expand(event, from, to, emptyList()) } returns listOf(occurrence)

        val result = useCase(requesterId = 1, targetUserId = 1, from, to)

        assertEquals(1, result.size)
        coVerify(exactly = 1) { rruleExpander.expand(event, from, to, emptyList()) }
    }

    @Test
    fun `returns both regular and recurring events merged and sorted by startAt`() = runTest {
        mockDefaults()
        val regularEvent = EventFixtures.event(id = 1, isRecurring = false, startAt = from.plusDays(2))
        val recurringEvent = EventFixtures.event(id = 2, isRecurring = true)
        val earlierOccurrence = EventFixtures.occurrence(eventId = 2, startAt = from.plusDays(1))
        val laterOccurrence = EventFixtures.occurrence(eventId = 2, startAt = from.plusDays(3))

        coEvery { eventRepository.getByUserIdAndRange(1, from, to) } returns listOf(regularEvent, recurringEvent)
        coEvery { eventExceptionRepository.getByEventIds(listOf(2), from, to) } returns emptyList()
        coEvery { rruleExpander.expand(recurringEvent, from, to, emptyList()) } returns listOf(
            earlierOccurrence,
            laterOccurrence,
        )

        val result = useCase(requesterId = 1, targetUserId = 1, from, to)

        assertEquals(3, result.size)
        assertEquals(from.plusDays(1), result[0].startAt)
        assertEquals(from.plusDays(2), result[1].startAt)
        assertEquals(from.plusDays(3), result[2].startAt)
    }

    // --- Visibility ---

    @Test
    fun `private events are hidden from strangers`() = runTest {
        mockDefaults(relation = ViewerRelation.Stranger)
        val privateEvent = EventFixtures.event(id = 1, visibility = Visibility.Private)
        val publicEvent = EventFixtures.event(id = 2, visibility = Visibility.Public, isRecurring = false, rrule = null)
        coEvery { eventRepository.getByUserIdAndRange(1, from, to) } returns listOf(privateEvent, publicEvent)
        coEvery { eventExceptionRepository.getByEventIds(emptyList(), from, to) } returns emptyList()

        val result = useCase(requesterId = 2, targetUserId = 1, from, to)

        assertEquals(1, result.size)
        assertEquals(publicEvent.title, result.first().title)
    }

    @Test
    fun `friends events are hidden from strangers but visible to friends`() = runTest {
        val friendsEvent = EventFixtures.event(visibility = Visibility.Friends, isRecurring = false, rrule = null)
        coEvery { eventRepository.getByUserIdAndRange(1, from, to) } returns listOf(friendsEvent)
        coEvery { eventExceptionRepository.getByEventIds(emptyList(), from, to) } returns emptyList()
        coEvery { eventCategoriesRepository.getAllByUser(1) } returns emptyList()

        coEvery { resolveViewerRelation(2, 1) } returns ViewerRelation.Stranger
        val strangerResult = useCase(requesterId = 2, targetUserId = 1, from, to)
        assertTrue(strangerResult.isEmpty())

        coEvery { resolveViewerRelation(3, 1) } returns ViewerRelation.Friend
        val friendResult = useCase(requesterId = 3, targetUserId = 1, from, to)
        assertEquals(1, friendResult.size)
    }

    @Test
    fun `owner can see all their own events regardless of visibility`() = runTest {
        mockDefaults(relation = ViewerRelation.Owner)
        val events = listOf(
            EventFixtures.event(id = 1, visibility = Visibility.Public, isRecurring = false, rrule = null),
            EventFixtures.event(id = 2, visibility = Visibility.Friends, isRecurring = false, rrule = null),
            EventFixtures.event(id = 3, visibility = Visibility.Private, isRecurring = false, rrule = null),
        )
        coEvery { eventRepository.getByUserIdAndRange(1, from, to) } returns events
        coEvery { eventExceptionRepository.getByEventIds(emptyList(), from, to) } returns emptyList()

        val result = useCase(requesterId = 1, targetUserId = 1, from, to)

        assertEquals(3, result.size)
    }

    @Test
    fun `category visibility restricts event visibility`() = runTest {
        val privateCategory = EventCategoryFixtures.category(id = 1, defaultVisibility = Visibility.Private)
        val event = EventFixtures.event(
            categoryId = 1,
            visibility = Visibility.Public, // event says public
            isRecurring = false,
            rrule = null,
        )
        coEvery { resolveViewerRelation(2, 1) } returns ViewerRelation.Friend
        coEvery { eventCategoriesRepository.getAllByUser(1) } returns listOf(privateCategory)
        coEvery { eventRepository.getByUserIdAndRange(1, from, to) } returns listOf(event)
        coEvery { eventExceptionRepository.getByEventIds(emptyList(), from, to) } returns emptyList()

        // category is private → effective visibility is private → friend can't see it
        val result = useCase(requesterId = 2, targetUserId = 1, from, to)

        assertTrue(result.isEmpty())
    }

    // --- Recurring specific ---

    @Test
    fun `fetches exceptions only for recurring events, not regular ones`() = runTest {
        mockDefaults()
        val regularEvent = EventFixtures.event(id = 1, isRecurring = false, rrule = null)
        val recurringEvent = EventFixtures.event(id = 2, isRecurring = true)
        coEvery { eventRepository.getByUserIdAndRange(1, from, to) } returns listOf(regularEvent, recurringEvent)
        coEvery { eventExceptionRepository.getByEventIds(listOf(2), from, to) } returns emptyList()
        coEvery { rruleExpander.expand(recurringEvent, from, to, emptyList()) } returns emptyList()

        useCase(requesterId = 1, targetUserId = 1, from, to)

        coVerify(exactly = 1) { eventExceptionRepository.getByEventIds(listOf(2), from, to) }
    }

    @Test
    fun `passes correct exceptions per event to expander`() = runTest {
        mockDefaults()
        val event1 = EventFixtures.event(id = 1, isRecurring = true)
        val event2 = EventFixtures.event(id = 2, isRecurring = true)
        val exceptionForEvent1 = EventExceptionFixtures.exception(eventId = 1)
        val exceptionForEvent2 = EventExceptionFixtures.exception(eventId = 2)
        coEvery { eventRepository.getByUserIdAndRange(1, from, to) } returns listOf(event1, event2)
        coEvery { eventExceptionRepository.getByEventIds(listOf(1, 2), from, to) } returns listOf(
            exceptionForEvent1,
            exceptionForEvent2,
        )
        coEvery { rruleExpander.expand(event1, from, to, listOf(exceptionForEvent1)) } returns emptyList()
        coEvery { rruleExpander.expand(event2, from, to, listOf(exceptionForEvent2)) } returns emptyList()

        useCase(requesterId = 1, targetUserId = 1, from, to)

        coVerify { rruleExpander.expand(event1, from, to, listOf(exceptionForEvent1)) }
        coVerify { rruleExpander.expand(event2, from, to, listOf(exceptionForEvent2)) }
    }

    @Test
    fun `skips expander call when there are no recurring events`() = runTest {
        mockDefaults()
        val regularEvent = EventFixtures.event(id = 1, isRecurring = false, rrule = null)
        coEvery { eventRepository.getByUserIdAndRange(1, from, to) } returns listOf(regularEvent)
        coEvery { eventExceptionRepository.getByEventIds(emptyList(), from, to) } returns emptyList()

        useCase(requesterId = 1, targetUserId = 1, from, to)

        coVerify(exactly = 0) { rruleExpander.expand(any(), any(), any(), any()) }
    }

    @Test
    fun `passes empty exceptions list to expander when no exceptions exist for event`() = runTest {
        mockDefaults()
        val recurringEvent = EventFixtures.event(id = 1, isRecurring = true)
        coEvery { eventRepository.getByUserIdAndRange(1, from, to) } returns listOf(recurringEvent)
        coEvery { eventExceptionRepository.getByEventIds(listOf(1), from, to) } returns emptyList()
        coEvery { rruleExpander.expand(recurringEvent, from, to, emptyList()) } returns emptyList()

        useCase(requesterId = 1, targetUserId = 1, from, to)

        coVerify { rruleExpander.expand(recurringEvent, from, to, emptyList()) }
    }

    // --- Edge cases ---

    @Test
    fun `result is sorted by startAt ascending`() = runTest {
        mockDefaults()
        val early = EventFixtures.event(id = 1, isRecurring = false, rrule = null, startAt = from.plusDays(3))
        val late = EventFixtures.event(id = 2, isRecurring = false, rrule = null, startAt = from.plusDays(1))
        coEvery { eventRepository.getByUserIdAndRange(1, from, to) } returns listOf(early, late)
        coEvery { eventExceptionRepository.getByEventIds(emptyList(), from, to) } returns emptyList()

        val result = useCase(requesterId = 1, targetUserId = 1, from, to)

        assertEquals(from.plusDays(1), result[0].startAt)
        assertEquals(from.plusDays(3), result[1].startAt)
    }

    @Test
    fun `handles multiple recurring events with separate exceptions`() = runTest {
        mockDefaults()
        val event1 = EventFixtures.event(id = 1, isRecurring = true)
        val event2 = EventFixtures.event(id = 2, isRecurring = true)
        val exception1 = EventExceptionFixtures.exception(id = 1, eventId = 1)
        val exception2 = EventExceptionFixtures.exception(id = 2, eventId = 2)
        val occurrences1 = listOf(EventFixtures.occurrence(eventId = 1, startAt = from.plusDays(1)))
        val occurrences2 = listOf(EventFixtures.occurrence(eventId = 2, startAt = from.plusDays(2)))
        coEvery { eventRepository.getByUserIdAndRange(1, from, to) } returns listOf(event1, event2)
        coEvery { eventExceptionRepository.getByEventIds(listOf(1, 2), from, to) } returns listOf(
            exception1,
            exception2,
        )
        coEvery { rruleExpander.expand(event1, from, to, listOf(exception1)) } returns occurrences1
        coEvery { rruleExpander.expand(event2, from, to, listOf(exception2)) } returns occurrences2

        val result = useCase(requesterId = 1, targetUserId = 1, from, to)

        assertEquals(2, result.size)
        coVerify { rruleExpander.expand(event1, from, to, listOf(exception1)) }
        coVerify { rruleExpander.expand(event2, from, to, listOf(exception2)) }
    }
}
