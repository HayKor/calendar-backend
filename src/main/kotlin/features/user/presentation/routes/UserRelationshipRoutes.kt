package com.haykor.features.user.presentation.routes

import com.haykor.core.common.presentation.principalContext
import com.haykor.core.exception.BadRequest
import com.haykor.features.user.domain.model.PendingRequestType
import com.haykor.features.user.domain.usecase.GetFriendsUseCase
import com.haykor.features.user.domain.usecase.GetPendingRequestsUseCase
import com.haykor.features.user.domain.usecase.SendFriendRequestUseCase
import com.haykor.features.user.domain.usecase.UpdateRelationshipUseCase
import com.haykor.features.user.presentation.model.UpdateRelationshipRequest
import com.haykor.features.user.presentation.model.toResponse
import com.haykor.features.user.presentation.model.toUserResponse
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.userRelationshipRoutes() {
    val sendFriendRequestUseCase by inject<SendFriendRequestUseCase>()
    val getFriendsUseCase by inject<GetFriendsUseCase>()
    val getPendingRequestsUseCase by inject<GetPendingRequestsUseCase>()
    val updateRelationshipUseCase by inject<UpdateRelationshipUseCase>()

    /**
     * Tag: User/Friends
     */
    authenticate("auth-jwt") {
        route("/user") {
            /**
             * Send friend request to user {id}
             */
            post("/{id}/friends") {
                val (requesterId) = principalContext()
                val addresseeId = call.parameters["id"]?.toIntOrNull()
                    ?: throw BadRequest("Invalid user id")
                val relationship = sendFriendRequestUseCase(requesterId, addresseeId)
                call.respond(HttpStatusCode.Created, relationship.toResponse())
            }

            route("/me/friends") {
                /**
                 * Get my friends list
                 */
                get {
                    val (userId) = principalContext()
                    val friends = getFriendsUseCase(userId)
                    call.respond(HttpStatusCode.OK, friends.map { it.toUserResponse() })
                }

                /**
                 * Get pending requests (incoming or outgoing)
                 */
                get("/requests") {
                    val (userId) = principalContext()
                    val type = PendingRequestType.from(call.request.queryParameters["type"])
                    val requests = getPendingRequestsUseCase(userId, type)
                    call.respond(HttpStatusCode.OK, requests.map { it.toResponse() })
                }

                /**
                 * Accept or decline request {id}
                 */
                patch("/{id}") {
                    val (userId) = principalContext()
                    val id = call.parameters["id"]?.toIntOrNull()
                        ?: throw BadRequest("Invalid relationship id")
                    val request = call.receive<UpdateRelationshipRequest>()
                    val relationship = updateRelationshipUseCase(userId, id, request.status)
                    call.respond(HttpStatusCode.OK, relationship.toResponse())
                }

                /**
                 * Remove friend or cancel request
                 */
                delete("/{id}") {
                    val (userId) = principalContext()
                    val id = call.parameters["id"]?.toIntOrNull()
                        ?: throw BadRequest("Invalid relationship id")
                    // TODO: DeleteRelationshipUseCase
                    call.respond(HttpStatusCode.NoContent)
                }
            }
        }
    }
}
