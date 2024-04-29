import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import routes.getPostRoute

fun main() {
    embeddedServer(Netty, port = 8080) {
        routing {
            getPostRoute()
        }
    }.start(wait = true)
}