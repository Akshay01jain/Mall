package com.sanmati

import com.sanmati.db.initDB
import com.sanmati.di.ConfigJWT
import com.sanmati.di.configureKoin
import com.sanmati.di.configureRouting
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.exposedLogger

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)

}

fun Application.module() {

    environment.monitor.subscribe(ApplicationStopping) {
        exposedLogger.info("Application stopping - waiting for requests to complete")
        // Give ongoing requests time to finish
        Thread.sleep(5000)
    }

    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
            isLenient = true
            explicitNulls = false
        })
    }

    ConfigJWT.init(environment.config)

    install(Authentication) {
        jwt("auth-jwt") {
            ConfigJWT.configure(this)
        }
    }


    configureKoin()
    initDB()
    configureRouting()

}
