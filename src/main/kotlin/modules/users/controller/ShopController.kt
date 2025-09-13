package com.sanmati.modules.users.controller

import com.sanmati.modules.users.dto.CreateShopInfoRequest
import com.sanmati.modules.users.services.ShopInfoServices
import com.sanmati.utils.respondError
import com.sanmati.utils.respondSuccess
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive

object ShopController {

    suspend fun createShopInfo(call: ApplicationCall) {
        try {
            val request = call.receive<CreateShopInfoRequest>()

            ShopInfoServices.createShopInfo(request).fold(
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
}