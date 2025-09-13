package com.sanmati.modules.products.controller

import com.sanmati.modules.products.dto.ProductRequest
import com.sanmati.modules.products.services.CategoryServices
import com.sanmati.modules.products.services.ProductServices
import com.sanmati.utils.respondError
import com.sanmati.utils.respondSuccess
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.plugins.CannotTransformContentToTypeException
import io.ktor.server.request.receive

object ProductController {

    suspend fun addProduct(call: ApplicationCall)
    {
        try {

            val request = call.receive<ProductRequest>()

            ProductServices.addProduct(request).fold(
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

    suspend fun getProductList(call: ApplicationCall) {
        try {

            val request = call.request.queryParameters["search"]

            ProductServices.getProductList(request).fold(
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

    suspend fun deleteProduct(call: ApplicationCall) {
        try {
            val product_id = call.request.queryParameters["product_id"]!!.toLong()

            ProductServices.deleteProduct(product_id).fold(
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