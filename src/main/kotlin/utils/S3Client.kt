package utils

import aws.sdk.kotlin.services.s3.*
import aws.sdk.kotlin.services.s3.model.BucketLocationConstraint
import aws.sdk.kotlin.services.s3.model.GetObjectAclRequest
import aws.smithy.kotlin.runtime.content.ByteStream
import kotlinx.coroutines.runBlocking
import java.util.UUID

val REGION = "us-east-2"
val BUCKET = "poc-photogram"
val KEY = "key"

fun main() = runBlocking {
    S3Client
        .fromEnvironment { region = REGION }
        .use { s3 ->
            setup(s3)

            println("Creating object $BUCKET/$KEY...")

            s3.putObject {
                bucket = BUCKET
                key = KEY
                body = ByteStream.fromString("Testing with the Kotlin SDK")
            }

            println("Object $BUCKET/$KEY created successfully!")

//            cleanUp(s3)
        }
}


//fun downloadFileFromS3(s3: S3Client, filenName: String){
//    s3.getObject(GetObjectAclRequest(BUCKET, filenName)
//
//}
suspend fun setup(s3: S3Client){
    println("Creating bucket $BUCKET")
    s3.createBucket {
        createBucketConfiguration {
            locationConstraint = BucketLocationConstraint.fromValue(REGION)
        }
    }
    println("Bucket $BUCKET created for region $REGION")
}

suspend fun cleanUp(s3: S3Client){
    println("Deleting object $BUCKET/$KEY")
    s3.deleteObject() {
        bucket = BUCKET
        key = KEY
    }
    println("Object $BUCKET/$KEY deleted successfully")
    s3.deleteBucket {
        bucket = BUCKET
    }
    println("Bucket $BUCKET deleted successfully")
}