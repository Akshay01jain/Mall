package com.sanmati.modules.products.dto

import kotlinx.serialization.Serializable

@Serializable
data class StockRequest(
    val productId: Long,
    val quantity: Double,
    val minStockLevel: Double? = null,
    val attributes : List<StockAttributesRequest>? = emptyList(),
    val notes: String? = null
)

@Serializable
data class StockResponse(
    val stockId: Long,
    val productId: Long,
    val currentStock: Double,
    val attributes: List<StockAttributesResponse>?,
    val updatedAt: String
)