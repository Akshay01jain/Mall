package com.sanmati.modules.invoices.controllers

import com.sanmati.modules.invoices.dto.CreateTaxRequest
import com.sanmati.modules.invoices.services.TaxServices
import com.sanmati.modules.products.services.CompanyServices
import com.sanmati.utils.respondError
import com.sanmati.utils.respondSuccess
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.plugins.CannotTransformContentToTypeException
import io.ktor.server.request.receive

object TaxControllers {

    suspend fun addTax(call: ApplicationCall) {
        try {

            val request = call.receive<CreateTaxRequest>()

            TaxServices.createTax(request).fold(
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

    suspend fun getTaxList(call: ApplicationCall) {
        try {

            val request = call.request.queryParameters["search"]

            TaxServices.getTaxList(request).fold(
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
}