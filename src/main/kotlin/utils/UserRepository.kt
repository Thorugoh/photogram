package utils

import java.sql.PreparedStatement
import java.sql.SQLException
import java.util.UUID

object UserRepository {
    fun insertUser (name: String, username: String, email: String){

    }
}

fun main (){
    UserRepository.insertUser("Bruno", "bhaetinger", "bhaetinger@gmail.com")
}