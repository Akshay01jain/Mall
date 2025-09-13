package com.sanmati.modules.products.dto


import kotlinx.serialization.Serializable

@Serializable
data class UnitRequest(
    val unitName: String,
    val unitCode: String
)


@Serializable
data class UnitResponse(
    val unitId: Int,
    val unitName: String,
    val unitCode: String?,
    val createdAt: String,
    val updatedAt: String
)
