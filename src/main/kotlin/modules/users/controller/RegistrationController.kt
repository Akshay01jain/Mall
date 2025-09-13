package com.sanmati.modules.users.controller

import com.sanmati.modules.users.dto.SendOTP
import com.sanmati.modules.users.dto.UserRequest
import com.sanmati.modules.users.services.RegistrationServices
import com.sanmati.utils.respondError
import com.sanmati.utils.respondSuccess
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.ContentTransformationException
import io.ktor.server.request.receive
import io.ktor.server.request.receiveText
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

object RegistrationController {

    suspend fun sendOTP(call: ApplicationCall)
    {
        try {
            val request = call.receive<SendOTP>()

            RegistrationServices.sendOTP(request).fold(
                onSuccess = {response ->
                    call.respondSuccess(status = HttpStatusCode.OK, message = response, data = "")
                },
                onFailure = {error ->
                    when (error) {
                        is IllegalArgumentException ->
                            call.respondError(
                                status = HttpStatusCode.Conflict,
                                message =  "${mapOf(" error " to error.message)}"
                            )

                        else ->
                            call.respondError(
                                status = HttpStatusCode.InternalServerError,
                                message = "Internal Server Error"
                            )
                    }
                }
            )
        }catch (e: ContentTransformationException) {
            call.respondError(
                status = HttpStatusCode.BadRequest,
                message = "${mapOf(" error " to " Invalid request format ")}"
            )
        }
    }

    suspend fun registerUser(call: ApplicationCall) {

        try {
            val rawBody = call.receiveText()

            val request = Json.decodeFromString<UserRequest>(rawBody) // Manual decode for debugging

            RegistrationServices.registration(request).fold(
                onSuccess = { response ->
                    call.respondSuccess(
                        status = HttpStatusCode.OK,
                        message = "User Create Successfully",
                        data = response
                    )
                },
                onFailure = { error ->
                    when (error) {
                        is IllegalArgumentException -> call.respondError(
                            status = HttpStatusCode.Conflict,
                            message = "${mapOf("error" to error.message)}"
                        )

                        else -> call.respondError(
                            status = HttpStatusCode.InternalServerError,
                            message = "Internal Server Error"
                        )
                    }
                }
            )

        } catch (e: SerializationException) {
            call.respondError(
                status = HttpStatusCode.BadRequest,
                message = mapOf("error" to "Invalid request format").toString()
            )
        }
    }




}