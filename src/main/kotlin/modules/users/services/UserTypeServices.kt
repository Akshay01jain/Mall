package com.sanmati.modules.users.services

import com.sanmati.modules.users.UserTypeTable
import com.sanmati.modules.users.dto.DeleteUserTypeRequest
import com.sanmati.modules.users.dto.UserTypeRequest
import com.sanmati.modules.users.dto.UserTypeResponse
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object UserTypeServices {

    fun createUserType(request: UserTypeRequest): Result<UserTypeResponse> = transaction {

        val exists = UserTypeTable.selectAll().where { UserTypeTable.name eq request.name }.count() > 0

        if (exists) {
            return@transaction Result.failure(
                IllegalArgumentException(" User type with name ${request.name} already exist.")
            )
        }

        try {
            val inserted = UserTypeTable.insert {
                it[name] = request.name
            }

            val record = UserTypeTable.selectAll().where { UserTypeTable.id eq inserted[UserTypeTable.id] }.single()

            return@transaction Result.success(
                UserTypeResponse(
                    id = record[UserTypeTable.id].toString(),
                    name = record[UserTypeTable.name],
                    createdAt = record[UserTypeTable.createdAt].toString()
                )
            )

        } catch (e: IllegalArgumentException) {
            return@transaction Result.failure(IllegalArgumentException("Failed to create user type: ${e.message}"))
        }
    }


    fun getAllUserType(): List<UserTypeResponse> = transaction {

        UserTypeTable.selectAll().map {
            UserTypeResponse(
                id = it[UserTypeTable.id].toString(),
                name = it[UserTypeTable.name],
                createdAt = it[UserTypeTable.createdAt].toString(),
            )
        }
    }

    fun getUserTypeName(request: String): Result<UserTypeResponse> = transaction {

        val exist = UserTypeTable.selectAll().where { UserTypeTable.name eq request }

        if (exist.count() > 0) {
            val row = exist.single()

            return@transaction Result.success(
                UserTypeResponse(
                    id = row[UserTypeTable.id].toString(),
                    name = row[UserTypeTable.name],
                    createdAt = row[UserTypeTable.createdAt].toString()
                )
            )
        } else {
            return@transaction Result.failure(IllegalArgumentException("No user type found with this Name."))
        }
    }

    fun deleteUserType(request: DeleteUserTypeRequest): Result<String> = transaction {

        try {
            val exist = UserTypeTable.selectAll().where { UserTypeTable.id eq request.id.toInt() }

            if (exist.singleOrNull() != null) {
                UserTypeTable.deleteWhere {
                   ( UserTypeTable.id eq request.id.toInt())
                }
                return@transaction  Result.success("UserType is deleted successfully. ")
            }
            else {
                return@transaction Result.success("No address found for this user.")
            }

        } catch (e: IllegalArgumentException) {
            return@transaction Result.failure(IllegalArgumentException(e))
        }
    }



}