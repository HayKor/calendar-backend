package com.haykor

import com.haykor.plugins.*
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureKoin()
    configureSecurity()
    configureDatabases()
    configureSwagger()
    configureSerialization()

    configureRoutes()
}
