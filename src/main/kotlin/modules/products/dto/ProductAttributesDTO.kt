package com.sanmati.modules.products.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProductAttributeRequest(
    val attributeName: String,
    val value: String
)

@Serializable
data class ProductAttributeResponse(
    val attribute_id: Long,
    val product_id: Long,
    val attribute_name: String,
    val value: String
)