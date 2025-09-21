package com.sanmati.modules.products.dto


import kotlinx.serialization.Serializable

@Serializable
data class SubcategoryRequest(
    val categoryId: Int,  // Parent category
    val name: String
)

@Serializable
data class SubcategoryResponse(
    val subcategoryId: Int,
    val categoryId: Int,
    val name: String,
    val createdAt: String
)
