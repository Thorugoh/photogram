package org.example

import data.UserDAO
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.example.plugins.configureSecurity
import org.example.utils.JWTService
import routes.getPostRoute

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    val jwtService = JWTService(this, UserDAO())
    configureSecurity(jwtService)

    routing {
        getPostRoute(jwtService)
    }
}