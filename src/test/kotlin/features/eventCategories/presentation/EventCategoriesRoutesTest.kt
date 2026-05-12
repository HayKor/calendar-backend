package features.eventCategories.presentation

import com.haykor.core.visibility.domain.model.Visibility
import com.haykor.features.eventCategories.domain.model.EventCategory
import com.haykor.features.eventCategories.domain.model.EventCategoryException
import com.haykor.features.eventCategories.domain.usecase.CreateEventCategoryUseCase
import com.haykor.features.eventCategories.domain.usecase.DeleteEventCategoryUseCase
import com.haykor.features.eventCategories.domain.usecase.GetAllEventCategoriesUseCase
import com.haykor.features.eventCategories.domain.usecase.GetEventCategoryByIdUseCase
import com.haykor.features.eventCategories.presentation.model.EventCategoryResponse
import com.haykor.features.eventCategories.presentation.routes.eventCategoriesRoutes
import common.BaseRouteTest
import common.generateTestToken
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.mockk.coEvery
import io.mockk.mockk
import org.koin.dsl.module
import kotlin.test.Test
import kotlin.test.assertEquals

class EventCategoriesRoutesTest : BaseRouteTest() {
    private val createEventCategoryUseCase = mockk<CreateEventCategoryUseCase>()
    private val getEventCategoryByIdUseCase = mockk<GetEventCategoryByIdUseCase>()
    private val getAllEventCategoriesUseCase = mockk<GetAllEventCategoriesUseCase>()
    private val deleteEventCategoryUseCase = mockk<DeleteEventCategoryUseCase>()

    override fun testModules() = listOf(
        module {
            single { createEventCategoryUseCase }
            single { getEventCategoryByIdUseCase }
            single { getAllEventCategoriesUseCase }
            single { deleteEventCategoryUseCase }
        },
    )

    override fun Routing.configureRoutes() {
        eventCategoriesRoutes()
    }

    private fun ApplicationTestBuilder.setup() = baseSetup()

    @Test
    fun `GET event_category - returns list`() = testApplication {
        setup()
        val expected = listOf(
            EventCategory(
                id = 1,
                userId = 1,
                name = "Work",
                visibility = Visibility.Private,
                colorHex = "#fff",
                iconName = "work",
            ),
        )
        coEvery { getAllEventCategoriesUseCase(1) } returns expected

        val response = client.get("/event_category") {
            header(HttpHeaders.Authorization, "Bearer ${generateTestToken(userId = 1)}")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.body<List<EventCategoryResponse>>()
        assertEquals(1, body.size)
        assertEquals("Work", body.first().name)
    }

    @Test
    fun `GET event_category id - not found throws 404`() = testApplication {
        setup()
        coEvery { getEventCategoryByIdUseCase(99, 1) } throws EventCategoryException.NotFound()

        val response = client.get("/event_category/99") {
            header(HttpHeaders.Authorization, "Bearer ${generateTestToken(userId = 1)}")
        }

        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `DELETE event_category id - forbidden returns 403`() = testApplication {
        setup()
        coEvery { deleteEventCategoryUseCase(1, 2) } throws EventCategoryException.Forbidden()

        val response = client.delete("/event_category/1") {
            header(HttpHeaders.Authorization, "Bearer ${generateTestToken(userId = 2)}")
        }

        assertEquals(HttpStatusCode.Forbidden, response.status)
    }
}
