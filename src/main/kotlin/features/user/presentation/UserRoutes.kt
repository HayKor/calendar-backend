package com.haykor.features.user.presentation

import com.haykor.features.user.domain.CreateUserUseCase
import com.haykor.features.user.domain.GetUserUseCase
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.userRoutes() {
    val createUserUseCase by inject<CreateUserUseCase>()
    val getUserUseCase by inject<GetUserUseCase>()

    route("/user") {
        post {
            val request = call.receive<UserCreateRequest>()
            try {
                val id = createUserUseCase.execute(request)
                call.respond(HttpStatusCode.Created, mapOf("id" to id))
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.Conflict, e.message ?: "Conflict")
            }
        }
        get("/{id}") {
            val id = call.parameters["id"]?.toInt() ?: return@get call.respond(HttpStatusCode.BadRequest)
            val user = getUserUseCase.execute(id) ?: return@get call.respond(HttpStatusCode.NotFound)
            call.respond(HttpStatusCode.OK, user)
        }
    }
}