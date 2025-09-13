package com.sanmati.di

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.sanmati.utils.respondError
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.jwt.JWTAuthenticationProvider
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.config.ApplicationConfig
import java.util.Date
import javax.naming.AuthenticationException

object ConfigJWT {
    private lateinit var secret: String
    private lateinit var issuer: String
    private lateinit var audience: String
    private var validityInMs: Long = 0L
    private lateinit var algorithm: Algorithm

    fun init(config: ApplicationConfig) {
        secret = config.property("jwt.secret").getString()
        issuer = config.property("jwt.domain").getString()
        audience = config.property("jwt.audience").getString()
        validityInMs = config.property("jwt.validity").getString().toLong()

        algorithm = Algorithm.HMAC256(secret)
    }

    fun generateToken(userId: String, username: String): String {
        return JWT.create()
            .withIssuer(issuer)
            .withAudience(audience)
            .withClaim("userId", userId)
            .withClaim("username", username)
            .withExpiresAt(getExpirationDate())
            .sign(algorithm)
    }

    private fun getExpirationDate(): Date {
        return Date(System.currentTimeMillis() + validityInMs)
    }

    fun configure(config: JWTAuthenticationProvider.Config) {
        config.verifier(
            JWT.require(algorithm)
                .withIssuer(issuer)
                .withAudience(audience)
                .build()
        )
        config.validate { credential ->
            if (credential.payload.getClaim("userId").asString().isNotEmpty()) {
                JWTPrincipal(credential.payload)
            } else {
                null
            }
        }
        config.challenge { _, _ ->
            call.respondError(
                status = HttpStatusCode.Unauthorized,
                message = "Token expired or invalid"
            )
        }
    }
}