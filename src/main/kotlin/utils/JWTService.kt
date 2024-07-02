package org.example.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import data.UserDAO
import io.ktor.server.application.*
import io.ktor.server.auth.jwt.*
import java.util.*

class JWTService(
    private val application: Application,
    private val userDAO: UserDAO
) {
    private val secret = getConfigProperty("jwt.secret")
    private val issuer = getConfigProperty("jwt.issuer")
    private val audience = getConfigProperty("jwt.audience")

    val realm = getConfigProperty("jwt.realm")

    val jwtVerifier: JWTVerifier = JWT
        .require(Algorithm.HMAC256(secret))
        .withAudience(audience)
        .withIssuer(issuer)
        .build()

    fun createJwtToken(username: String): String? {
        return JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("username", username)
            .withExpiresAt(Date(System.currentTimeMillis() + 60000))
            .sign(Algorithm.HMAC256(secret))
    }

    fun customValidator(credential: JWTCredential): JWTPrincipal? {
        val username = extractUsername(credential)
        val foundUser = username?.let(userDAO::getUserByUsername)

        return foundUser?.let {
            if (audienceMatches(credential)) {
                JWTPrincipal(credential.payload)
            } else {
                null
            }
        }
    }

    private fun audienceMatches(credential: JWTCredential): Boolean {
        return credential.payload.audience.contains(audience)
    }

    private fun extractUsername(credential: JWTCredential): String {
        return credential.payload.getClaim("username").asString()
    }

    private fun getConfigProperty(propertyName: String): String {
        return application.environment.config.property(propertyName).getString()
    }
}