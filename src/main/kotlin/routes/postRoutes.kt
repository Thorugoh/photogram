package routes

import aws.smithy.kotlin.runtime.content.asByteStream
import data.PostDAO
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import model.Post
import utils.DatabaseConnection
import utils.S3
import java.io.File
import java.util.Date
import java.util.UUID

fun Route.getPostRoute() {
    get("/posts") {
        call.respondText("Posts")
    }
    post("/upload") {
        val multipartData = call.receiveMultipart()
        var description: String? = null
        var photoFile: File? = null
        var s3FileURL: String? = null

        multipartData.forEachPart {part ->
            when(part) {
                is PartData.FormItem -> {
                    if(part.name == "description") {
                        description = part.value
                    }
                }
                is PartData.FileItem -> {
                    if(part.name == "photo") {
                        val fileBytes = part.streamProvider().readAllBytes()

                        val postId = UUID.randomUUID().toString()
                        val file = File("/Users/vhugo/photogram-downloads/$postId.jpeg")

                        file.writeBytes(fileBytes)
                        try {
                            s3FileURL = S3().uploadFile(file.asByteStream())

                        }catch (e: Exception){
                             print(e.message)
                        }

                       s3FileURL?.let {url ->
                           PostDAO().insertPost("ce9bc80f-7c1f-49c6-b89a-9003e0dbedfc",
                               Post(
                                   postId,
                                   url,
                                   "",
                                   Date()
                               )
                           )
                       }

                        photoFile = file
                    }
                }
                else -> Unit
            }
            part.dispose()
        }

        if(description != null && photoFile != null) {
            call.respond(HttpStatusCode.OK, s3FileURL!!)
        } else {
            call.respond(HttpStatusCode.BadRequest, "Missing description or photo")
        }
    }
}