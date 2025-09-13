package com.sanmati.modules.users.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserTypeRequest(
    val name : String
)

@Serializable
data class UserTypeUpdateRequest(
    val id : String,
    val name : String
)


@Serializable
data class DeleteUserTypeRequest(
    val id : String
)

@Serializable
data class UserTypeResponse(
    val id: String,
    val name: String,
    val createdAt: String
)
