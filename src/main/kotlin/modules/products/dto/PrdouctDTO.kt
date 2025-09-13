package com.sanmati.modules.products.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductRequest(
    val company_id: Int,
    val category_id: Int? = null,
    val unit_id: Int? = null,
    val name: String,
    val hsn_Code: String? = null,
    val item_Code: String? = null,
    val description: String? = null,
    val attributes: List<ProductAttributeRequest> = emptyList(),
    val prices: List<ProductPriceRequest> = emptyList(),
    val images: List<ProductImageRequest> = emptyList(),
    val stocks: List<StockRequest> = emptyList()
)


@Serializable
data class ProductResponse(
    val product_id: Long,
    val company_id: Int,
    val category_id: Int?,
    val unit_id: Int?,
    val name: String,
    val hsn_code: String?,
    val item_code: String?,
    val description: String?,
    val created_at: String, // ISO-8601 format
    val updated_at: String,
    val attributes: List<ProductAttributeResponse>,
    val prices: List<ProductPriceResponse>,
    val images: List<ProductImageResponse>,
    val stocks: List<StockResponse>
)