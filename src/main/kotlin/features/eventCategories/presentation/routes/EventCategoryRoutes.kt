package com.haykor.features.eventCategories.presentation.routes

import com.haykor.core.common.presentation.principalContext
import com.haykor.core.exception.BadRequest
import com.haykor.features.eventCategories.domain.repository.CreateEventCategoryParams
import com.haykor.features.eventCategories.domain.usecase.CreateEventCategoryUseCase
import com.haykor.features.eventCategories.domain.usecase.DeleteEventCategoryUseCase
import com.haykor.features.eventCategories.domain.usecase.GetAllEventCategoriesUseCase
import com.haykor.features.eventCategories.domain.usecase.GetEventCategoryByIdUseCase
import com.haykor.features.eventCategories.presentation.model.CreateEventCategoryRequest
import com.haykor.features.eventCategories.presentation.model.EventCategoryResponse
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.eventCategoriesRoutes() {
    val createEventCategoryUseCase by inject<CreateEventCategoryUseCase>()
    val getEventCategoryByIdUseCase by inject<GetEventCategoryByIdUseCase>()
    val getAllEventCategoriesUseCase by inject<GetAllEventCategoriesUseCase>()
    val deleteEventCategoryUseCase by inject<DeleteEventCategoryUseCase>()

    /**
     * Tags:
     *   - Event/Event_Category
     */
    authenticate("auth-jwt") {
        route("event_category") {
            post {
                val request = call.receive<CreateEventCategoryRequest>()
                val (userId) = principalContext()
                val eventCategory = createEventCategoryUseCase(
                    CreateEventCategoryParams(
                        userId = userId,
                        name = request.name,
                        visibility = request.visibility,
                        colorHex = request.colorHex,
                        iconName = request.iconName,
                    ),
                )
                call.respond(
                    HttpStatusCode.Created,
                    EventCategoryResponse(
                        name = eventCategory.name,
                        userId = userId,
                        visibility = eventCategory.visibility,
                        colorHex = eventCategory.colorHex,
                        iconName = eventCategory.iconName,
                    ),
                )
            }

            get {
                val (userId) = principalContext()
                val categories = getAllEventCategoriesUseCase(userId)
                call.respond(
                    HttpStatusCode.OK,
                    categories.map { category ->
                        EventCategoryResponse(
                            name = category.name,
                            userId = userId,
                            visibility = category.visibility,
                            colorHex = category.colorHex,
                            iconName = category.iconName,
                        )
                    },
                )
            }

            route("{id}") {
                get {
                    val (userId) = principalContext()
                    val id = call.parameters["id"]?.toIntOrNull()
                        ?: throw BadRequest("Invalid category id")
                    val category = getEventCategoryByIdUseCase(id, userId)
                    call.respond(
                        HttpStatusCode.OK,
                        EventCategoryResponse(
                            name = category.name,
                            userId = userId,
                            visibility = category.visibility,
                            colorHex = category.colorHex,
                            iconName = category.iconName,
                        ),
                    )
                }

                delete {
                    val (userId) = principalContext()
                    val id = call.parameters["id"]?.toIntOrNull()
                        ?: throw BadRequest("Invalid category id")
                    deleteEventCategoryUseCase(id, userId)
                    call.respond(HttpStatusCode.NoContent)
                }
            }
        }
    }
}
