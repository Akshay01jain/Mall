package com.sanmati.modules.products.dto

import kotlinx.serialization.Serializable

@Serializable
data class CategoryRequest(
    val parentId: Int? = null,
    val name: String
)


@Serializable
data class CategoryResponse(
    val categoryId: Int,
    val parentId: Int?,
    val name: String,
    val createdAt: String
)