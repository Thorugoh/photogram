package utils

import aws.sdk.kotlin.services.s3.*
import aws.sdk.kotlin.services.s3.model.GetObjectRequest
import aws.sdk.kotlin.services.s3.model.PutObjectRequest
import aws.smithy.kotlin.runtime.content.asByteStream
import aws.smithy.kotlin.runtime.content.writeToFile
import kotlinx.coroutines.runBlocking
import java.io.File
import java.util.UUID

val REGION = "us-east-2"
val BUCKET = "poc-photogram"
class S3 {
    suspend fun uploadFile(objectPath: String) = runBlocking {
        val metadataVal = mutableMapOf<String, String>()
        metadataVal["myVal"] = "test"

        val request = PutObjectRequest {
            bucket = BUCKET
            key = "${UUID.randomUUID()}.jpg"
            metadata = metadataVal
            body = File(objectPath).asByteStream()
        }

        S3Client { region = REGION }.use { s3 ->
            val response = s3.putObject(request)
            println("Tag information is ${response.eTag}")
        }
    }

    suspend fun downloadFileFromS3(filename: String, path: String){
        val request = GetObjectRequest {
            key=filename
            bucket=BUCKET
        }

        S3Client { region = REGION }.use {s3 ->
            s3.getObject(request) {resp ->
                val myFile = File(path)
                if(myFile.createNewFile()){
                    resp.body?.writeToFile(myFile)
                    println("Read $filename from $BUCKET")
                }
            }

        }
    }
}

suspend fun main () {
    S3().uploadFile("/Users/vhugo/photogram-downloads/tree.jpg")
}