package com.sanmati.modules.products.dto

import kotlinx.serialization.Serializable

@Serializable
data class CompanyRequest(
    val name : String
)


@Serializable
data class CompanyResponse(
    val company_id: Int,
    val name: String,
    val createdAt: String
)