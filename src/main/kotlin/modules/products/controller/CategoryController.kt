package com.sanmati.modules.products.controller.modules.products.controller

import com.sanmati.modules.products.dto.CategoryRequest
import com.sanmati.modules.products.services.CategoryServices
import com.sanmati.utils.respondError
import com.sanmati.utils.respondSuccess
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.plugins.CannotTransformContentToTypeException
import io.ktor.server.request.receive
import kotlin.text.toInt

object CategoryController {

    suspend fun addUnit(call: ApplicationCall) {
        try {
            val request = call.receive<CategoryRequest>()

            CategoryServices.addCategory(request).fold(
                onSuccess = { response ->
                    call.respondSuccess(
                        status = HttpStatusCode.OK,
                        message = response,
                        data = ""
                    )
                },
                onFailure = { error ->
                    when (error) {
                        is IllegalArgumentException ->
                            call.respondError(
                                status = HttpStatusCode.Conflict,
                                message = "${mapOf(" error " to error.message)}"
                            )

                        else ->
                            call.respondError(
                                status = HttpStatusCode.InternalServerError,
                                message = "Internal Server Error"
                            )
                    }
                }
            )
        } catch (e: CannotTransformContentToTypeException) {
            call.respondError(
                status = HttpStatusCode.BadRequest,
                message = "${mapOf("error" to "Invalid request format: ${e.message}")}"
            )
        } catch (e: Exception) {
            call.respondError(
                status = HttpStatusCode.BadRequest,
                message = "${mapOf(" error " to " Invalid request format : $e")}"
            )
        }
    }

    suspend fun getCategoryList(call: ApplicationCall) {
        try {

            val request = call.request.queryParameters["search"]

            CategoryServices.getCategoryList(request).fold(
                onSuccess = { response ->
                    call.respondSuccess(status = HttpStatusCode.OK, message = "", data = response)
                },
                onFailure = { error ->
                    when (error) {
                        is IllegalArgumentException ->
                            call.respondError(
                                status = HttpStatusCode.BadRequest,
                                message = "${error.message}"
                            )

                        else ->
                            call.respondError(
                                status = HttpStatusCode.InternalServerError,
                                message = "Internal Server Error"
                            )
                    }
                }
            )

        } catch (e: IllegalArgumentException) {
            call.respondError(
                status = HttpStatusCode.BadRequest,
                message = "${mapOf(" error " to " Invalid request format ")}"
            )
        }

    }

    suspend fun deleteCategory(call: ApplicationCall) {
        try {
            val id = call.request.queryParameters["id"]!!.toInt()

            CategoryServices.deleteCategory(id).fold(
                onSuccess = { response ->
                    call.respondSuccess(status = HttpStatusCode.OK, message = response, data = "")
                },
                onFailure = { error ->
                    when (error) {
                        is IllegalArgumentException ->
                            call.respondError(
                                status = HttpStatusCode.BadRequest,
                                message = "${mapOf(" error " to error.message)}"
                            )

                        else ->
                            call.respondError(
                                status = HttpStatusCode.InternalServerError,
                                message = "Internal Server Error"
                            )
                    }

                }
            )
        }catch (e : IllegalArgumentException)
        {
            call.respondError(
                status = HttpStatusCode.BadRequest,
                message = "${mapOf(" error " to " Invalid request format ")}"
            )
        }
    }
}