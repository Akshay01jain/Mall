package com.sanmati.modules.products.controller

import com.sanmati.modules.products.dto.CompanyRequest
import com.sanmati.modules.products.services.CompanyServices
import com.sanmati.utils.respondError
import com.sanmati.utils.respondSuccess
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.plugins.CannotTransformContentToTypeException
import io.ktor.server.request.receive
import kotlin.text.toInt

object CompanyController {

    suspend fun addCompany(call: ApplicationCall) {
        try {

            val request = call.receive<CompanyRequest>()

            CompanyServices.addCompany(request).fold(
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

    suspend fun getCompanyList(call: ApplicationCall) {
        try {

            val request = call.request.queryParameters["search"]

            CompanyServices.getCompanyList(request).fold(
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

    suspend fun deleteCompany(call: ApplicationCall) {
        try {
            val id = call.request.queryParameters["id"]!!.toInt()

            CompanyServices.deleteCompany(id).fold(
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