package com.sanmati.modules.users.controller

import com.sanmati.modules.users.dto.DeleteAddressRequest
import com.sanmati.modules.users.dto.DeleteUserTypeRequest
import com.sanmati.modules.users.dto.UserTypeRequest
import com.sanmati.modules.users.services.UserAddressServices
import com.sanmati.modules.users.services.UserTypeServices
import com.sanmati.utils.respondError
import com.sanmati.utils.respondSuccess
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.ContentTransformationException
import io.ktor.server.request.receive


object UserTypeController {

    //Create UserType
    suspend fun create(call: ApplicationCall) {
        try {
            val request = call.receive<UserTypeRequest>()

            UserTypeServices.createUserType(request).fold(
                onSuccess = { response ->
                    call.respondSuccess(
                        status = HttpStatusCode.Created,
                        message = "${request.name} usertype created successfully.",
                        data = mapOf("user_type" to response)
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
        } catch (e: ContentTransformationException) {
            call.respondError(
                status = HttpStatusCode.BadRequest,
                message = "${mapOf(" error " to " Invalid request format ")}"
            )
        }
    }

    //get All User Type List
    suspend fun getAllUserType(call: ApplicationCall) {
        try {
            val list = UserTypeServices.getAllUserType()

            if (list.isNotEmpty()) {
                call.respondSuccess(list, message = "List of user types")
            } else {
                call.respondError("Data Not Found", status = HttpStatusCode.NotFound)
            }
        } catch (e: Exception) {
            print("$e")
            call.respondError("Invalid request format", status = HttpStatusCode.BadRequest)
        }
    }

    //get UserType by Name
    suspend fun getUserTypeName(call: ApplicationCall) {
        try {
            val request = call.request.queryParameters["name"] ?: run {
                call.respondError(
                    status = HttpStatusCode.BadRequest,
                    message = "Name parameter is required."
                )
                return
            }

            UserTypeServices.getUserTypeName(request).fold(
                onSuccess = { response ->
                    call.respondSuccess(
                        status = HttpStatusCode.OK,
                        message = "User Type found.",
                        data = response
                    )
                },
                onFailure = {
                    call.respondError(
                        status = HttpStatusCode.NotFound,
                        message = "No user type found."
                    )
                }
            )
        } catch (e: Exception) {
            call.respondError(
                status = HttpStatusCode.BadRequest,
                message = "Invalid request format."
            )
        }
    }

    suspend fun deleteUserType(call: ApplicationCall)
    {
        try {
            val request = DeleteUserTypeRequest(
                id = call.request.queryParameters["id"]!!,
            )

            UserTypeServices.deleteUserType(request).fold(
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
