package com.haykor.features.user.presentation

import com.haykor.core.common.presentation.principalContext
import com.haykor.core.exception.BadRequest
import com.haykor.features.user.domain.model.CreateUserParams
import com.haykor.features.user.domain.usecase.CreateUserUseCase
import com.haykor.features.user.domain.usecase.GetUserUseCase
import com.haykor.features.user.presentation.model.UserCreateRequest
import com.haykor.features.user.presentation.model.UserResponse
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.userRoutes() {
    val createUserUseCase by inject<CreateUserUseCase>()
    val getUserUseCase by inject<GetUserUseCase>()

    /**
     * Tag: User
     */
    route("/user") {
        post {
            val request = call.receive<UserCreateRequest>()
            val user = createUserUseCase(
                CreateUserParams(
                    request.name,
                    request.email,
                    request.password,
                    isVerified = false,
                ),
            )
            call.respond(
                HttpStatusCode.Created,
                UserResponse(
                    id = user.id,
                    email = user.email,
                    name = user.name,
                ),
            )
        }
        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull() ?: throw BadRequest("Invalid id")
            val user = getUserUseCase(id)
            call.respond(
                HttpStatusCode.OK,
                UserResponse(
                    id = user.id,
                    email = user.email,
                    name = user.name,
                ),
            )
        }
        authenticate("auth-jwt") {
            get("me") {
                // TODO: return user's info
                val (userId) = principalContext()
                call.respond(mapOf("userId" to "$userId"))
            }
        }
    }
}
