package com.sanmati.modules.invoices.dto

import kotlinx.serialization.Serializable

// Request DTOs
@Serializable
data class CreateTaxRequest(
    val name: String,
    val rate: Double,
    val isActive: Boolean = true
)
@Serializable
data class UpdateTaxRequest(
    val name: String? = null,
    val rate: Double? = null,
    val isActive: Boolean? = null
)

@Serializable
data class TaxResponse(
    val taxId: Int,
    val name: String,
    val rate: Double,
    val isActive: Boolean,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

@Serializable
data class TaxListResponse(
    val taxes: List<TaxResponse>,
    val totalCount: Int
)