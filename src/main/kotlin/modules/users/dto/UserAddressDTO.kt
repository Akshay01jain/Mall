package com.sanmati.modules.users.dto

import kotlinx.serialization.Serializable

@Serializable
data class AddAddressRequest(
    val userId: String,
    val addressName: String? = null,
    val addressLine1: String,
    val addressLine2: String? = null,
    val city: String,
    val state: String,
    val country: String = "India",
    val pin_code: String,
    val landmark: String? = null,
    val isDefault: Boolean = false
)

@Serializable
data class AddressRequest(
    val userId: String? = null
)

@Serializable
data class DeleteAddressRequest(
    val userId: String,
    val ad_id: Int
)


@Serializable
data class UserAddressResponse(
    val ad_id: Int,
    val userId: String,
    val addressName: String,
    val addressLine1: String,
    val addressLine2: String?,
    val city: String,
    val state: String,
    val country: String,
    val pin_code: String,
    val landmark: String?,
    val isDefault: Boolean
)