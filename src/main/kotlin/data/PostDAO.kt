package data

import model.Post
import model.User
import utils.DatabaseConnection
import java.sql.PreparedStatement
import java.sql.SQLException
import java.util.Date

class PostDAO {

    val connection = DatabaseConnection.getConnection()

    fun insertPost(userId: String, post: Post){
        val insertSQL = "INSERT INTO posts (id, image_url, description, user_id) VALUES (?, ?, ?, ?)"
        var preparedStatement: PreparedStatement? = null

        try {
            if(connection != null) {
                preparedStatement = connection.prepareStatement(insertSQL)
                preparedStatement.setString(1, post.id)
                preparedStatement.setString(2, post.imageUrl)
                preparedStatement.setString(3, post.description)
                preparedStatement.setString(4, userId)

                preparedStatement.executeUpdate()

                println("Post created successfully!")
            }else {
                println("Failed to create post")
            }
        }catch (e: SQLException) {
            e.printStackTrace()
        }finally {
            preparedStatement?.close()
            connection?.close()
        }

    }

    fun getAllByUser(userId: String): User? {
        val connection = DatabaseConnection.getConnection()

        val selectSQL = ("SELECT * FROM posts WHERE user_id = ?")
        if(connection != null) {
            val preparedStatement = connection.prepareStatement(selectSQL)
            preparedStatement.setString(1, userId);
            try {
                val resultSet = preparedStatement.executeQuery()
                if(resultSet.next()) {
                    return User(
                        resultSet.getString("id"),
                        resultSet.getString("name"),
                        resultSet.getString("username"),
                        resultSet.getString("email"),
                        null,
                        null
                    )
                }
            }catch (e: SQLException) {
                e.printStackTrace()
            }
        }

        return null
    }
}

fun main(){
    println(PostDAO().insertPost("ce9bc80f-7c1f-49c6-b89a-9003e0dbedfc", Post("id-0", "url", "desc", Date())))
}