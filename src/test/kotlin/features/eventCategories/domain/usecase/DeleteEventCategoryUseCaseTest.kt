package features.eventCategories.domain.usecase

import com.haykor.features.eventCategories.domain.model.EventCategoryException
import com.haykor.features.eventCategories.domain.repository.EventCategoriesRepository
import com.haykor.features.eventCategories.domain.usecase.DeleteEventCategoryUseCase
import features.eventCategories.domain.model.EventCategoryFixtures
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFailsWith

class DeleteEventCategoryUseCaseTest {
    private val repository: EventCategoriesRepository = mockk()
    private val useCase = DeleteEventCategoryUseCase(repository)

    @Test
    fun `deletes category when owner requests it`() = runTest {
        val category = EventCategoryFixtures.category(id = 1, userId = 10)
        coEvery { repository.getById(1) } returns category
        coEvery { repository.deleteById(1) } returns true

        useCase(id = 1, requestingUserId = 10)

        coVerify(exactly = 1) { repository.deleteById(1) }
    }

    @Test
    fun `throws NotFound when category does not exist`() = runTest {
        coEvery { repository.getById(99) } returns null

        assertFailsWith<EventCategoryException.NotFound> {
            useCase(id = 99, requestingUserId = 10)
        }
        coVerify(exactly = 0) { repository.deleteById(any()) }
    }

    @Test
    fun `throws Forbidden when requester is not the owner`() = runTest {
        val category = EventCategoryFixtures.category(id = 1, userId = 10)
        coEvery { repository.getById(1) } returns category

        assertFailsWith<EventCategoryException.Forbidden> {
            useCase(id = 1, requestingUserId = 99)
        }
        coVerify(exactly = 0) { repository.deleteById(any()) }
    }
}
