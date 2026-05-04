package features.eventCategories

import com.haykor.features.eventCategories.domain.repository.EventCategoriesRepository
import com.haykor.features.eventCategories.domain.usecase.CreateEventCategoryUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CreateEventCategoryUseCaseTest {
    private val repository: EventCategoriesRepository = mockk()
    private val useCase = CreateEventCategoryUseCase(repository)

    @Test
    fun `creates and returns category`() = runTest {
        val params = EventCategoryFixtures.createParams()
        val expected = EventCategoryFixtures.category()

        coEvery { repository.create(params) } returns expected

        val result = useCase(params)

        assertEquals(expected, result)
        coVerify(exactly = 1) { repository.create(params) }
    }
}
