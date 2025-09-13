package com.sanmati.modules.users.controller

import com.sanmati.modules.users.dto.LoginRequest
import com.sanmati.modules.users.services.LoginServices
import com.sanmati.modules.users.services.RegistrationServices
import com.sanmati.utils.respondError
import com.sanmati.utils.respondSuccess
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receiveText
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

object LoginController {

    //login User
    suspend fun loginUser(call: ApplicationCall)
    {
        try {
            val rawBody = call.receiveText()
            val request = Json.decodeFromString<LoginRequest>(rawBody) // Manual decode for debugging

            LoginServices.login(request).fold(
                onSuccess = { response ->
                    call.respondSuccess(
                        status = HttpStatusCode.OK,
                        message = "User Login Successfully.",
                        data = response
                    )
                },
                onFailure = { error ->
                    when (error) {
                        is IllegalArgumentException -> call.respondError(
                            status = HttpStatusCode.BadRequest,
                            message = " ${error.message}"
                        )

                        else -> call.respondError(
                            status = HttpStatusCode.InternalServerError,
                            message = "Internal Server Error"
                        )
                    }
                }
            )

        }catch (e: SerializationException)
        {
            println("$e")
            call.respondError(
                status = HttpStatusCode.BadRequest,
                message = mapOf("error" to "Invalid request format").toString()
            )
        }
    }
}