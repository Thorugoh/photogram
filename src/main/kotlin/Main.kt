package org.example

import io.ktor.server.application.*
import io.ktor.server.routing.*
import routes.getPostRoute

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    routing {
        getPostRoute()
    }
}