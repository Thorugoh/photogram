package utils

import aws.sdk.kotlin.runtime.auth.credentials.ProfileCredentialsProvider
import aws.sdk.kotlin.services.s3.*
import aws.sdk.kotlin.services.s3.model.GetObjectRequest
import aws.sdk.kotlin.services.s3.model.PutObjectRequest
import aws.smithy.kotlin.runtime.content.ByteStream
import aws.smithy.kotlin.runtime.content.asByteStream
import aws.smithy.kotlin.runtime.content.writeToFile
import kotlinx.coroutines.runBlocking
import java.io.File
import java.util.UUID

val REGION = "us-east-2"
val BUCKET = "poc-photogram"
val profileName = "user1"

class S3 {
    suspend fun uploadFile(objectPath: String) = runBlocking {
        val metadataVal = mutableMapOf<String, String>()
        metadataVal["myVal"] = "test"

        return@runBlocking uploadFile(File(objectPath).asByteStream())
    }

    suspend fun uploadFile(fileBytes: ByteStream) = runBlocking{
        val metadataVal = mutableMapOf<String, String>()
        metadataVal["myVal"] = "test"

        val key = "${UUID.randomUUID()}.jpg"
        val request = PutObjectRequest {
            bucket = BUCKET
            this.key = key
            metadata = metadataVal
            body = fileBytes
        }

        S3Client { region = REGION
            credentialsProvider = ProfileCredentialsProvider(profileName)
        }.use { s3 ->
            val response = s3.putObject(request)
            println("Tag information is ${response.eTag}")
        }

        return@runBlocking "https://$BUCKET.s3.$REGION.amazonaws.com/$key"
    }

    suspend fun downloadFileFromS3(filename: String, path: String){
        val request = GetObjectRequest {
            key=filename
            bucket=BUCKET
        }

        S3Client {
            region = REGION
            credentialsProvider = ProfileCredentialsProvider(profileName)
        }.use {s3 ->
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