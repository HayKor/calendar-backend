package com.haykor.features.user.presentation.routes

import com.haykor.core.common.presentation.principalContext
import com.haykor.core.exception.BadRequest
import com.haykor.features.user.domain.repository.CreateUserParams
import com.haykor.features.user.domain.usecase.CreateUserUseCase
import com.haykor.features.user.domain.usecase.GetUserUseCase
import com.haykor.features.user.presentation.model.UserCreateRequest
import com.haykor.features.user.presentation.model.toUserResponse
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
                user.toUserResponse(),
            )
        }
        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull() ?: throw BadRequest("Invalid id")
            val user = getUserUseCase(id)
            call.respond(
                HttpStatusCode.OK,
                user.toUserResponse(),
            )
        }
        authenticate("auth-jwt") {
            get("me") {
                val (userId) = principalContext()
                val user = getUserUseCase(userId)
                call.respond(
                    HttpStatusCode.OK,
                    user.toUserResponse(),
                )
            }
        }
    }
}
