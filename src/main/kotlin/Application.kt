package com.haykor

import com.haykor.plugins.*
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureCallLogging()
    configureKoin()
    configureAuth(environment.config)
    configureDatabases()
    configureSwagger()
    configureSerialization()

    configureRoutes()
}
