package com.haykor.features.event.presentation.routes.util

import com.haykor.core.common.presentation.principalContext
import com.haykor.core.exception.BadRequest
import com.haykor.features.event.domain.usecase.*
import com.haykor.features.event.presentation.model.CreateEventRequest
import com.haykor.features.event.presentation.model.UpdateEventRequest
import com.haykor.features.event.presentation.model.UpdateOccurrenceRequest
import com.haykor.features.event.presentation.model.mapper.toParams
import com.haykor.features.event.presentation.model.mapper.toResponse
import com.haykor.features.event.presentation.util.parseRRuleInput
import io.ktor.http.*
import io.ktor.openapi.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.openapi.*
import io.ktor.utils.io.*
import kotlinx.datetime.LocalDate
import org.koin.ktor.ext.inject
import java.time.OffsetDateTime

@OptIn(ExperimentalKtorApi::class)
fun Route.eventRoutes() {
    val createEventUseCase by inject<CreateEventUseCase>()
    val getEventByIdUseCase by inject<GetEventByIdUseCase>()
    val getEventsInRangeUseCase by inject<GetEventsInRangeUseCase>()
    val updateEventUseCase by inject<UpdateEventUseCase>()
    val deleteEventUseCase by inject<DeleteEventUseCase>()
    val updateEventOccurrenceUseCase by inject<UpdateEventOccurrenceUseCase>()
    val deleteEventOccurrenceUseCase by inject<DeleteEventOccurrenceUseCase>()

    /**
     * Tag: Event
     */
    authenticate("auth-jwt") {
        route("/events") {
            /**
             * Create event
             */
            post {
                val (userId) = principalContext()
                val request = call.receive<CreateEventRequest>()
                val rrule = call.parseRRuleInput()
                val event = createEventUseCase(request.toParams(userId, rrule))
                call.respond(HttpStatusCode.Created, event.toResponse())
            }.describe {
                requestBody {
                    this.schema = jsonSchema<CreateEventRequest>()
                }
            }

            /**
             * Get occurrences in range
             */
            get {
                val (requesterId) = principalContext()
                val qp = call.request.queryParameters
                val targetUserId = qp["targetUserId"]?.toIntOrNull()
                    ?: throw BadRequest("targetUserId is required")
                val from = qp["from"]?.let { OffsetDateTime.parse(it) }
                    ?: throw BadRequest("from is required")
                val to = qp["to"]?.let { OffsetDateTime.parse(it) }
                    ?: throw BadRequest("to is required")

                val occurrences = getEventsInRangeUseCase(requesterId, targetUserId, from, to)
                call.respond(HttpStatusCode.OK, occurrences.map { it.toResponse() })
            }.describe {
                parameters {
                    query("targetUserId") { required = true }
                    query("from") {
                        description = "Range start (ISO-8601 datetime with offset)"
                        example = GenericElementString("2026-05-01T00:00:00+03:00")
                        required = true
                    }

                    query("to") {
                        description = "Range end (ISO-8601 datetime with offset)"
                        example = GenericElementString("2026-06-01T00:00:00+03:00")
                        required = true
                    }
                }
            }

            route("/{id}") {
                /**
                 * Get event by id
                 */
                get {
                    val (requesterId) = principalContext()
                    val id = call.parameters["id"]?.toIntOrNull() ?: throw BadRequest("Invalid id")
                    val event = getEventByIdUseCase(requesterId, id)
                    call.respond(HttpStatusCode.OK, event.toResponse())
                }

                /**
                 * Update whole event
                 */
                patch {
                    val (userId) = principalContext()
                    val id = call.parameters["id"]?.toIntOrNull() ?: throw BadRequest("Invalid id")
                    val request = call.receive<UpdateEventRequest>()
                    val event = updateEventUseCase(userId, id, request.toParams(call.parseRRuleInput()))
                    call.respond(HttpStatusCode.OK, event.toResponse())
                }

                /**
                 * Delete whole event
                 */
                delete {
                    val (userId) = principalContext()
                    val id = call.parameters["id"]?.toIntOrNull() ?: throw BadRequest("Invalid id")
                    deleteEventUseCase(userId, id)
                    call.respond(HttpStatusCode.NoContent)
                }

                route("/occurrences/{date}") {
                    /**
                     * Update single occurrence
                     */
                    patch {
                        val (userId) = principalContext()
                        val id = call.parameters["id"]?.toIntOrNull() ?: throw BadRequest("Invalid id")
                        val date = call.parameters["date"]?.let {
                            runCatching { LocalDate.parse(it) }.getOrElse { throw BadRequest("Invalid date, expected YYYY-MM-DD") }
                        } ?: throw BadRequest("date is required")
                        val request = call.receive<UpdateOccurrenceRequest>()
                        val exception = updateEventOccurrenceUseCase(userId, id, date, request.toParams())
                        call.respond(HttpStatusCode.OK, exception.toResponse())
                    }

                    /**
                     * Cancel single occurrence
                     */
                    delete {
                        val (userId) = principalContext()
                        val id = call.parameters["id"]?.toIntOrNull() ?: throw BadRequest("Invalid id")
                        val date = call.parameters["date"]?.let {
                            runCatching { LocalDate.parse(it) }.getOrElse { throw BadRequest("Invalid date, expected YYYY-MM-DD") }
                        } ?: throw BadRequest("date is required")
                        deleteEventOccurrenceUseCase(userId, id, date)
                        call.respond(HttpStatusCode.NoContent)
                    }
                }
            }
        }
    }
}
