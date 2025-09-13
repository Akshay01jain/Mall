package com.sanmati.modules.users.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val userMobileNumber: String,
    val userPassword: String? = "",
)


@Serializable
data class LoginResponse(
    val userId: String,
    val userTypeId: Int,
    val userTypeName: String,
    val username: String,
    val userMobileNumber: String,
    val userActive: Boolean,
    val phoneVerified: Boolean,
    val lastLoginAt: String,
    val isDeleted: Boolean,
    val createdAt: String,
    val updateAt: String,
    val token : String
)
