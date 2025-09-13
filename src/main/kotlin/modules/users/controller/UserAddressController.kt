package com.sanmati.modules.users.controller

import com.sanmati.modules.users.dto.AddAddressRequest
import com.sanmati.modules.users.dto.AddressRequest
import com.sanmati.modules.users.dto.DeleteAddressRequest
import com.sanmati.modules.users.services.UserAddressServices
import com.sanmati.utils.respondError
import com.sanmati.utils.respondSuccess
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.receiveType
import io.ktor.server.request.receive

object UserAddressController {

    //add address
    suspend fun addAddress(call: ApplicationCall) {
        try {

            val request = call.receive<AddAddressRequest>()

            UserAddressServices.addAddress(request).fold(
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

        } catch (e: IllegalArgumentException) {
            call.respondError(
                status = HttpStatusCode.BadRequest,
                message = "${mapOf(" error " to " Invalid request format ")}"
            )
        }
    }

    //get address by id and without id
    suspend fun addressList(call: ApplicationCall) {
        try {

            val request = AddressRequest(
                userId = call.request.queryParameters["userId"]
            )

            UserAddressServices.addressList(request).fold(
                onSuccess = { response ->
                    call.respondSuccess(status = HttpStatusCode.OK, message = "", data = response)
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

        } catch (e: IllegalArgumentException) {
            call.respondError(
                status = HttpStatusCode.BadRequest,
                message = "${mapOf(" error " to " Invalid request format ")}"
            )
        }

    }

    //delete address of the user
    suspend fun deleteUserAddress(call: ApplicationCall)
    {
        try {
            val request = DeleteAddressRequest(
                    userId = call.request.queryParameters["userId"]!!,
                    ad_id = call.request.queryParameters["ad_id"]!!.toInt()
                    )

            UserAddressServices.deleteUserAddress(request).fold(
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