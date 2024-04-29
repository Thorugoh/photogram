package utils

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

object DatabaseConnection {
    private var connection: Connection? = null;

    init {
        val url = "jdbc:mysql://localhost:3306/photogram"
        val username = System.getenv("DB_USERNAME") ?: "username"
        val password = System.getenv("DB_PASSWORD") ?: "pass"

        println(username)
        println(password)
        try {
            Class.forName("com.mysql.cj.jdbc.Driver")
            connection = DriverManager.getConnection(url, username, password)
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    fun getConnection(): Connection? {
        return connection
    }
}