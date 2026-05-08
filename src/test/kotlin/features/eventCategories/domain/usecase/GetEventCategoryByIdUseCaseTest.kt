package features.eventCategories.domain.usecase

import com.haykor.features.eventCategories.domain.model.EventCategoryException
import com.haykor.features.eventCategories.domain.repository.EventCategoriesRepository
import com.haykor.features.eventCategories.domain.usecase.GetEventCategoryByIdUseCase
import features.eventCategories.domain.model.EventCategoryFixtures
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GetEventCategoryByIdUseCaseTest {
    private val repository: EventCategoriesRepository = mockk()
    private val useCase = GetEventCategoryByIdUseCase(repository)

    @Test
    fun `returns category when owner requests it`() = runTest {
        val category = EventCategoryFixtures.category(id = 1, userId = 10)
        coEvery { repository.getById(1) } returns category

        val result = useCase(id = 1, requestingUserId = 10)

        assertEquals(category, result)
    }

    @Test
    fun `throws NotFound when category does not exist`() = runTest {
        coEvery { repository.getById(99) } returns null

        assertFailsWith<EventCategoryException.NotFound> {
            useCase(id = 99, requestingUserId = 10)
        }
    }

    @Test
    fun `throws Forbidden when requester is not the owner`() = runTest {
        val category = EventCategoryFixtures.category(id = 1, userId = 10)
        coEvery { repository.getById(1) } returns category

        assertFailsWith<EventCategoryException.Forbidden> {
            useCase(id = 1, requestingUserId = 99)
        }
    }
}
