package com.sanmati.modules.users.dto

import kotlinx.serialization.Serializable


@Serializable
data class SendOTP(
    val userMobileNumber: String
)

@Serializable
data class UserRequest(
    val username: String,
    val otp : String,
    val userMobileNumber: String,
    val userPassword: String,
    val userTypeName: String
)

@Serializable
data class UserResponse(
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
    val updateAt: String
)
