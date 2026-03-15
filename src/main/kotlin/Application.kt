package com.haykor

import com.haykor.plugins.configureDI
import com.haykor.plugins.configureDatabases
import com.haykor.plugins.configureRoutes
import com.haykor.plugins.configureSecurity
import com.haykor.plugins.configureSerialization
import com.haykor.plugins.configureSwagger
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureDatabases()
    configureRoutes()
    configureSwagger()
    configureDI()
    configureSerialization()
    configureSecurity()
}
