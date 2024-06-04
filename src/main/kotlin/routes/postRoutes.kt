package routes

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.getPostRoute() {
    get("/posts") {

        call.respondText("Posts")
    }
}