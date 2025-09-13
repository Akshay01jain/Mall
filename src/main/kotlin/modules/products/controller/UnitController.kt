package com.sanmati.modules.products.controller

import com.sanmati.modules.products.dto.UnitRequest
import com.sanmati.modules.products.services.UnitServices
import com.sanmati.modules.users.dto.AddressRequest
import com.sanmati.modules.users.dto.DeleteAddressRequest
import com.sanmati.modules.users.services.UserAddressServices
import com.sanmati.utils.respondError
import com.sanmati.utils.respondSuccess
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.plugins.CannotTransformContentToTypeException
import io.ktor.server.request.receive

object UnitController {

    suspend fun addUnit(call: ApplicationCall) {
        try {

            val request = call.receive<UnitRequest>()

            UnitServices.addUnit(request).fold(
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

    suspend fun getUnitList(call: ApplicationCall) {
        try {

            val request = call.request.queryParameters["search"]

            UnitServices.getUnitList(request).fold(
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

    suspend fun updateUnit(call: ApplicationCall) {

        val unit_id = call.parameters["unitId"]?.toIntOrNull()
        if (unit_id == null) {
            call.respondError(
                status = HttpStatusCode.BadRequest,
                message = "Invalid Id"
            )
            return
        }
        val request = call.receive<UnitRequest>()
        val result = UnitServices.updateUnit(unit_id, request)
        result.fold(
            onSuccess = { call.respondSuccess(HttpStatusCode.OK, message = "$it") },
            onFailure = { call.respondError(status = HttpStatusCode.BadRequest) }
        )
    }

    suspend fun deleteUnit(call: ApplicationCall) {
        try {
            val unit_id = call.parameters["id"]!!.toInt()

            UnitServices.deleteUnit(unit_id).fold(
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