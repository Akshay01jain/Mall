package com.sanmati.utils

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond


suspend inline fun <reified T> ApplicationCall.respondSuccess(
    data: T? = null,
    message: String = "Success",
    status: HttpStatusCode = HttpStatusCode.OK
) {
    respond(status, ApiResponse.success(data, message, status))
}

suspend fun ApplicationCall.respondError(
    message: String = "Error",
    status: HttpStatusCode = HttpStatusCode.BadRequest,
) {
    respond(status, ApiResponse.error(message, status, ))
}