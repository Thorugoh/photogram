package org.example.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import org.example.utils.JWTService

fun Application.configureSecurity(
    jwtService: JWTService
) {
    authentication {
        jwt {
            realm = jwtService.realm
            verifier(jwtService.jwtVerifier)

            validate {credential ->
                jwtService.customValidator(credential)
            }
        }

        jwt("another-auth") {
            realm = jwtService.realm
            verifier(jwtService.jwtVerifier)

            validate {credential ->
                jwtService.customValidator(credential)
            }
        }
    }
}