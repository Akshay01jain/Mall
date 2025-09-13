package com.sanmati.modules.users.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateShopInfoRequest(
    val userId: String,
    val shopName: String,
    val gstNumber: String? = "",
    val shopImage: String? = "",
    val addressId: Int? = 0
)

@Serializable
data class CreateShopInfoResponse(
    val shopId : String,
    val userId: String,
    val shopName: String,
    val gstNumber: String,
    val shopImage: String,
    val addressId: Int,
    val createdAt : String,
    val updatedAt : String
)