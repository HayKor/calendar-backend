package features.eventCategories

import com.haykor.features.eventCategories.domain.repository.EventCategoriesRepository
import com.haykor.features.eventCategories.domain.usecase.GetAllEventCategoriesUseCase
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetAllEventCategoriesUseCaseTest {
    private val repository: EventCategoriesRepository = mockk()
    private val useCase = GetAllEventCategoriesUseCase(repository)

    @Test
    fun `returns all categories for user`() = runTest {
        val categories = listOf(
            EventCategoryFixtures.category(id = 1, userId = 10),
            EventCategoryFixtures.category(id = 2, userId = 10, name = "Personal"),
        )
        coEvery { repository.getAllByUser(10) } returns categories

        val result = useCase(userId = 10)

        assertEquals(2, result.size)
        assertEquals(categories, result)
    }

    @Test
    fun `returns empty list when user has no categories`() = runTest {
        coEvery { repository.getAllByUser(10) } returns emptyList()

        val result = useCase(userId = 10)

        assertTrue(result.isEmpty())
    }
}
