package com.sanmati.modules.products.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProductImageRequest(
    val imageUrl: String,
    val isPrimary: Boolean
)

@Serializable
data class ProductImageResponse(
    val image_id: Long,
    val product_id: Long,
    val image_url: String,
    val is_primary: Boolean
)