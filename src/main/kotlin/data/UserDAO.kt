package data

import model.User
import utils.DatabaseConnection
import java.sql.PreparedStatement
import java.sql.SQLException

class UserDAO {
    private val connection = DatabaseConnection.getConnection()

    fun insertUser(user: User){
        val insertSQL = "INSERT INTO users (name, username, email, id) VALUES (?, ?, ?, ?)"
        var preparedStatement: PreparedStatement? = null

        try {
            if(connection != null) {
                preparedStatement = connection.prepareStatement(insertSQL)
                preparedStatement.setString(1, user.name)
                preparedStatement.setString(2, user.username)
                preparedStatement.setString(3, user.email)
                preparedStatement.setString(4, user.id)
                preparedStatement.executeUpdate()

                println("User inserted successfully!")
            }else {
                println("Failed to insert user")
            }
        }catch (e: SQLException) {
            e.printStackTrace()
        }finally {
            preparedStatement?.close()
            connection?.close()
        }

    }

    fun getUserById(id: String): User? {
        val selectSQL = ("SELECT * FROM users WHERE id = ?")
        if(connection != null) {
            val preparedStatement = connection.prepareStatement(selectSQL)
            preparedStatement.setString(1, id);
            try {
                val resultSet = preparedStatement.executeQuery()
                if(resultSet.next()) {
                    return User(
                        resultSet.getString("id"),
                        resultSet.getString("name"),
                        resultSet.getString("username"),
                        resultSet.getString("email")
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
    println(UserDAO().getUserById("ce9bc80f-7c1f-49c6-b89a-9003e0dbedfc"))
}