import aws.sdk.kotlin.services.s3.*
import aws.sdk.kotlin.services.s3.model.BucketLocationConstraint
import aws.sdk.kotlin.services.s3.model.GetObjectRequest
import aws.smithy.kotlin.runtime.content.ByteStream
import aws.smithy.kotlin.runtime.content.writeToFile
import kotlinx.coroutines.runBlocking
import java.io.File
import java.util.UUID

//import aws.sdk.kotlin.services.s3.S3Client
//import io.ktor.server.engine.*
//import io.ktor.server.netty.*
//import io.ktor.server.routing.*
//import routes.getPostRoute
//
//fun main() {
//    embeddedServer(Netty, port = 8080) {
//        routing {
//            getPostRoute()
//        }
//    }.start(wait = true)
//}

val REGION = "us-east-2"
val BUCKET = "poc-photogram"
val KEY = "key"
fun main(): Unit = runBlocking {
    S3Client
        .fromEnvironment { region = REGION }
        .use { s3 ->
            downloadFileFromS3(s3,"tree.jpg", "${System.getProperty("user.home")}${File.separator}photogram-downloads${File.separator}tree.jpg")
        }
}

suspend fun downloadFileFromS3(s3: S3Client, filename: String, path: String){
    val request = GetObjectRequest {
        key=filename
        bucket=BUCKET
    }
    s3.getObject(request) {resp ->
        val myFile = File(path)
        if(myFile.createNewFile()){
            resp.body?.writeToFile(myFile)
            println("Read $filename from $BUCKET")
        }
    }
}

suspend fun setup(s3: S3Client) {
    println("Creating bucket $BUCKET...")
    s3.createBucket {
        bucket = BUCKET
            createBucketConfiguration {
                locationConstraint = BucketLocationConstraint.fromValue(REGION)
            }
    }
    println("Bucket $BUCKET created successfully!")
}

suspend fun cleanUp(s3: S3Client) {
    println("Deleting object $BUCKET/$KEY...")
    s3.deleteObject {
        bucket = BUCKET
        key = KEY
    }
    println("Object $BUCKET/$KEY deleted successfully!")

    println("Deleting bucket $BUCKET...")
    s3.deleteBucket {
        bucket = BUCKET
    }
    println("Bucket $BUCKET deleted successfully!")
}