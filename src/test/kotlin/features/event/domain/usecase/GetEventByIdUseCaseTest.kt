package features.event.domain.usecase

import com.haykor.core.visibility.domain.model.ViewerRelation
import com.haykor.core.visibility.domain.model.Visibility
import com.haykor.core.visibility.domain.usecase.ResolveViewerRelationUseCase
import com.haykor.features.event.domain.model.Event
import com.haykor.features.event.domain.model.EventError
import com.haykor.features.event.domain.repository.EventRepository
import com.haykor.features.event.domain.usecase.GetEventByIdUseCase
import com.haykor.features.eventCategories.domain.repository.EventCategoriesRepository
import features.event.domain.model.EventFixtures
import features.eventCategories.domain.model.EventCategoryFixtures
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFailsWith

class GetEventByIdUseCaseTest {

    private val eventRepository = mockk<EventRepository>()
    private val eventCategoriesRepository = mockk<EventCategoriesRepository>()
    private val resolveViewerRelation = mockk<ResolveViewerRelationUseCase>()
    private val useCase = GetEventByIdUseCase(eventRepository, eventCategoriesRepository, resolveViewerRelation)

    private fun mockDefaults(
        event: Event = EventFixtures.event(),
        relation: ViewerRelation = ViewerRelation.Owner,
    ) {
        coEvery { eventRepository.getById(event.id) } returns event
        coEvery { resolveViewerRelation(any(), event.userId) } returns relation
        coEvery { eventCategoriesRepository.getById(any()) } returns null
    }

    // --- Happy path ---

    @Test
    fun `returns event when requester is owner`() = runTest {
        val event = EventFixtures.event(visibility = Visibility.Private)
        mockDefaults(event = event, relation = ViewerRelation.Owner)

        val result = useCase(requesterId = 1, eventId = event.id)

        assertEquals(event, result)
    }

    @Test
    fun `returns public event to stranger`() = runTest {
        val event = EventFixtures.event(visibility = Visibility.Public)
        mockDefaults(event = event, relation = ViewerRelation.Stranger)

        val result = useCase(requesterId = 2, eventId = event.id)

        assertEquals(event, result)
    }

    @Test
    fun `returns friends event to friend`() = runTest {
        val event = EventFixtures.event(visibility = Visibility.Friends)
        mockDefaults(event = event, relation = ViewerRelation.Friend)

        val result = useCase(requesterId = 2, eventId = event.id)

        assertEquals(event, result)
    }

    // --- Not found ---

    @Test
    fun `throws NotFound when event does not exist`() = runTest {
        coEvery { eventRepository.getById(99) } returns null

        assertFailsWith<EventError.NotFound> {
            useCase(requesterId = 1, eventId = 99)
        }
    }

    // --- Visibility: event-level ---

    @Test
    fun `throws Forbidden when private event requested by stranger`() = runTest {
        val event = EventFixtures.event(visibility = Visibility.Private)
        mockDefaults(event = event, relation = ViewerRelation.Stranger)

        assertFailsWith<EventError.Forbidden> {
            useCase(requesterId = 2, eventId = event.id)
        }
    }

    @Test
    fun `throws Forbidden when private event requested by friend`() = runTest {
        val event = EventFixtures.event(visibility = Visibility.Private)
        mockDefaults(event = event, relation = ViewerRelation.Friend)

        assertFailsWith<EventError.Forbidden> {
            useCase(requesterId = 2, eventId = event.id)
        }
    }

    @Test
    fun `throws Forbidden when friends event requested by stranger`() = runTest {
        val event = EventFixtures.event(visibility = Visibility.Friends)
        mockDefaults(event = event, relation = ViewerRelation.Stranger)

        assertFailsWith<EventError.Forbidden> {
            useCase(requesterId = 2, eventId = event.id)
        }
    }

    // --- Visibility: category-level ---

    @Test
    fun `throws Forbidden when category is private even if event is public`() = runTest {
        val privateCategory = EventCategoryFixtures.category(id = 1, defaultVisibility = Visibility.Private)
        val event = EventFixtures.event(categoryId = 1, visibility = Visibility.Public)
        mockDefaults(event = event, relation = ViewerRelation.Friend)
        coEvery { eventCategoriesRepository.getById(1) } returns privateCategory

        assertFailsWith<EventError.Forbidden> {
            useCase(requesterId = 2, eventId = event.id)
        }
    }

    @Test
    fun `throws Forbidden when category is friends and requester is stranger`() = runTest {
        val friendsCategory = EventCategoryFixtures.category(id = 1, defaultVisibility = Visibility.Friends)
        val event = EventFixtures.event(categoryId = 1, visibility = Visibility.Public)
        mockDefaults(event = event, relation = ViewerRelation.Stranger)
        coEvery { eventCategoriesRepository.getById(1) } returns friendsCategory

        assertFailsWith<EventError.Forbidden> {
            useCase(requesterId = 2, eventId = event.id)
        }
    }

    @Test
    fun `event without category uses only event visibility`() = runTest {
        val event = EventFixtures.event(categoryId = null, visibility = Visibility.Public)
        mockDefaults(event = event, relation = ViewerRelation.Stranger)

        val result = useCase(requesterId = 2, eventId = event.id)

        assertEquals(event, result)
        coVerify(exactly = 0) { eventCategoriesRepository.getById(any()) }
    }

    // --- Relation resolution ---

    @Test
    fun `resolves relation using event owner's userId, not eventId`() = runTest {
        val event = EventFixtures.event(id = 42, userId = 7)
        mockDefaults(event = event, relation = ViewerRelation.Owner)

        useCase(requesterId = 1, eventId = 42)

        coVerify { resolveViewerRelation(1, 7) }
    }
}
