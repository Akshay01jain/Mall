package com.sanmati.modules.products.controller

import com.sanmati.modules.products.dto.SubcategoryRequest
import com.sanmati.modules.products.dto.SubcategoryResponse
import com.sanmati.modules.products.services.SubcategoryServices
import com.sanmati.modules.products.tables.SubcategoryTable
import com.sanmati.utils.respondError
import com.sanmati.utils.respondSuccess
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.plugins.CannotTransformContentToTypeException
import io.ktor.server.request.receive
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object SubcategoryController {

    // Add Subcategory
    suspend fun addSubcategory(call: ApplicationCall) {
        try {
            val request = call.receive<SubcategoryRequest>()

            SubcategoryServices.addSubcategory(request).fold(
                onSuccess = { message ->
                    call.respondSuccess(
                        status = HttpStatusCode.OK,
                        message = message,
                        data = ""
                    )
                },
                onFailure = { error ->
                    when (error) {
                        is IllegalArgumentException ->
                            call.respondError(
                                status = HttpStatusCode.Conflict,
                                message = mapOf("error" to error.message).toString()
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
                message = mapOf("error" to "Invalid request format: ${e.message}").toString()
            )
        } catch (e: Exception) {
            call.respondError(
                status = HttpStatusCode.BadRequest,
                message = mapOf("error" to "Invalid request format: ${e.message}").toString()
            )
        }
    }

    // Get all subcategories OR subcategories by category
    suspend fun getSubcategories(call: ApplicationCall) {
        try {
            val categoryIdParam = call.request.queryParameters["categoryId"]

            if (categoryIdParam != null) {
                val categoryId = categoryIdParam.toIntOrNull()
                    ?: return call.respondError(
                        status = HttpStatusCode.BadRequest,
                        message = mapOf("error" to "Invalid category ID").toString()
                    )

                SubcategoryServices.getSubcategoriesByCategory(categoryId).fold(
                    onSuccess = { subcategories ->
                        call.respondSuccess(
                            status = HttpStatusCode.OK,
                            message = "",
                            data = subcategories
                        )
                    },
                    onFailure = { error ->
                        when (error) {
                            is IllegalArgumentException ->
                                call.respondError(
                                    status = HttpStatusCode.BadRequest,
                                    message = mapOf("error" to error.message).toString()
                                )
                            else ->
                                call.respondError(
                                    status = HttpStatusCode.InternalServerError,
                                    message = "Internal Server Error"
                                )
                        }
                    }
                )
            } else {
                // Optionally, get all subcategories if no categoryId provided
                val allSubcategories = transaction {
                    SubcategoryTable.selectAll().map {
                        SubcategoryResponse(
                            subcategoryId = it[SubcategoryTable.subcategory_id],
                            categoryId = it[SubcategoryTable.category_id],
                            name = it[SubcategoryTable.name],
                            createdAt = it[SubcategoryTable.createdAt].toString()
                        )
                    }
                }

                call.respondSuccess(
                    status = HttpStatusCode.OK,
                    message = "",
                    data = allSubcategories
                )
            }
        } catch (e: Exception) {
            call.respondError(
                status = HttpStatusCode.BadRequest,
                message = mapOf("error" to "Invalid request format: ${e.message}").toString()
            )
        }
    }

    // Delete Subcategory
    suspend fun deleteSubcategory(call: ApplicationCall) {
        try {
            val idParam = call.request.queryParameters["id"]
                ?: return call.respondError(
                    status = HttpStatusCode.BadRequest,
                    message = mapOf("error" to "Missing subcategory ID").toString()
                )

            val subcategoryId = idParam.toIntOrNull()
                ?: return call.respondError(
                    status = HttpStatusCode.BadRequest,
                    message = mapOf("error" to "Invalid subcategory ID").toString()
                )

            SubcategoryServices.deleteSubcategory(subcategoryId).fold(
                onSuccess = { message ->
                    call.respondSuccess(
                        status = HttpStatusCode.OK,
                        message = message,
                        data = ""
                    )
                },
                onFailure = { error ->
                    when (error) {
                        is IllegalArgumentException ->
                            call.respondError(
                                status = HttpStatusCode.BadRequest,
                                message = mapOf("error" to error.message).toString()
                            )
                        else ->
                            call.respondError(
                                status = HttpStatusCode.InternalServerError,
                                message = "Internal Server Error"
                            )
                    }
                }
            )
        } catch (e: Exception) {
            call.respondError(
                status = HttpStatusCode.BadRequest,
                message = mapOf("error" to "Invalid request format: ${e.message}").toString()
            )
        }
    }
}
