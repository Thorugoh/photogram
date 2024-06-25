package model

data class User(
    val id: String,
    val name: String,
    val username: String,
    val email: String,
    val password: String?,
    val salt: String?
)
