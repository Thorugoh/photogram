package model

import java.util.Date

data class Post(
    val id: String,
    val imageUrl: String,
    val description: String,
    val date: Date,
)
