package com.sanmati.utils

import io.ktor.http.HttpStatusCode
import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val status: Int,
    val message: String,
    val data: T? = null
) {
    companion object {
        fun <T> success(
            data: T? = null,
            message: String = "Success",
            status: HttpStatusCode = HttpStatusCode.OK
        ) = ApiResponse(
            status = status.value,
            message = message,
            data = data
        )

        fun error(
            message: String = "Error",
            status: HttpStatusCode = HttpStatusCode.BadRequest,
        ) = ApiResponse<Unit>(
            status = status.value,
            message = message
        )
    }
}