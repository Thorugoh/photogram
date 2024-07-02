package routes

import aws.smithy.kotlin.runtime.content.asByteStream
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import data.PostDAO
import data.UserDAO
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import io.ktor.util.Identity.decode
import io.ktor.util.pipeline.*
import model.Post
import model.User
import org.example.utils.JWTService
import org.example.utils.generateHash
import org.example.utils.generateRandomSalt
import utils.S3
import java.io.File
import java.util.Date
import java.util.UUID


fun Route.getPostRoute(jwtService: JWTService) {
    post("/login") {
        val user = call.receiveParameters()
        if (user["username"] == null || user["password"] == null) {
            call.respond(HttpStatusCode.BadRequest, "Missing username or password")
            return@post
        }


        val dbUser = UserDAO().getUserByUsername(user["username"]!!)
        if (dbUser?.salt == null || dbUser.password == null || user["password"] == null) {
            call.respond(HttpStatusCode.BadRequest, "Incorrect credentials")
            return@post
        }


        val hashedRequest = generateHash(user["password"]!!, dbUser.salt)

        if (hashedRequest != dbUser.password) {
            call.respond(HttpStatusCode.BadRequest, "Incorrect credentials")
            return@post
        }

        val token = jwtService.createJwtToken(user["username"]!!)

        token?.let {
            call.respondText(it)
        } ?: call.respond(HttpStatusCode.Unauthorized)
    }

    post("/signup") {
        val user = call.receiveParameters()
        if (user["username"] == null || user["password"] == null) {
            call.respond(HttpStatusCode.BadRequest, "Missing username or password")
            return@post
        }
        val salt = generateRandomSalt().toString()
        val hashed = generateHash(user["password"]!!, salt)

        UserDAO().insertUser(
            User(UUID.randomUUID().toString(), user["name"]!!, user["username"]!!, user["email"]!!, null, null),
            salt,
            hashed,
        )

        call.respond(HttpStatusCode.OK)
    }

    authenticate {
        get("/posts") {
            call.respondText("Posts")
        }
    }


    authenticate {
        post("/upload") {
            val multipartData = call.receiveMultipart()
            var description: String? = null
            var photoFile: File? = null
            var s3FileURL: String? = null

            multipartData.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> {
                        if (part.name == "description") {
                            description = part.value
                        }
                    }

                    is PartData.FileItem -> {
                        if (part.name == "photo") {
                            val fileBytes = part.streamProvider().readAllBytes()

                            val postId = UUID.randomUUID().toString()
                            val file = File("/Users/vhugo/photogram-downloads/$postId.jpeg")

                            file.writeBytes(fileBytes)
                            try {
                                s3FileURL = S3().uploadFile(file.asByteStream())

                            } catch (e: Exception) {
                                print(e.message)
                            }

                            val user = extractPrincipal(call, UserDAO())

                            if (user != null) {
                                s3FileURL?.let { url ->
                                    PostDAO().insertPost(
                                        user.id,
                                        Post(
                                            postId,
                                            url,
                                            "",
                                            Date()
                                        )
                                    )
                                }
                            }


                            photoFile = file
                        }
                    }

                    else -> Unit
                }
                part.dispose()
            }

            if (description != null && photoFile != null) {
                call.respond(HttpStatusCode.OK, s3FileURL!!)
            } else {
                call.respond(HttpStatusCode.BadRequest, "Missing description or photo")
            }
        }
    }
}


fun extractPrincipal(call: ApplicationCall, userDAO: UserDAO): User? {
    val principal = call.principal<JWTPrincipal>()
    return userDAO.getUserByUsername(principal?.payload?.getClaim("username")?.asString() ?: "")
}