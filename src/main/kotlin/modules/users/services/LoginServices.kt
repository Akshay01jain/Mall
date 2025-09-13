package com.sanmati.modules.users.services

import com.sanmati.di.ConfigJWT
import com.sanmati.modules.users.dto.LoginRequest
import com.sanmati.modules.users.dto.LoginResponse
import com.sanmati.modules.users.tables.UsersTable
import com.sanmati.modules.users.tables.UsersTable.lastLoginAt
import com.sanmati.utils.verifyPassword
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime

object LoginServices {

    //login
    fun login(request: LoginRequest): Result<LoginResponse> = transaction {

        try {
            val users = UsersTable.selectAll()
                .where { (UsersTable.userMobileNumber eq request.userMobileNumber) and (UsersTable.userActive eq true) }
                .singleOrNull() ?: return@transaction Result.failure(IllegalArgumentException("User Not Found"))

            val isPasswordCheck = verifyPassword(request.userPassword!!, users[UsersTable.userPassword])

            if (!isPasswordCheck) {
                return@transaction Result.failure(IllegalArgumentException("Invalid Password"))
            }

            if (!users[UsersTable.userActive]) {
                return@transaction Result.failure(IllegalArgumentException("User account is inactive"))
            }

            val token = ConfigJWT.generateToken(
                userId = users[UsersTable.userId],
                username = users[UsersTable.username]
            )

            UsersTable.update({ UsersTable.userId eq users[UsersTable.userId] }) {
                it[lastLoginAt] = LocalDateTime.now()
            }

            return@transaction Result.success(
                LoginResponse(
                    userId = users[UsersTable.userId],
                    userTypeId = users[UsersTable.userTypeId],
                    userTypeName = users[UsersTable.userTypeName],
                    username = users[UsersTable.username],
                    userMobileNumber = users[UsersTable.userMobileNumber],
                    userActive = users[UsersTable.userActive],
                    phoneVerified = users[UsersTable.phoneVerified],
                    lastLoginAt = users[UsersTable.lastLoginAt].toString(),
                    isDeleted = users[UsersTable.isDeleted],
                    createdAt = users[UsersTable.createdAt].toString(),
                    updateAt = users[UsersTable.updatedAt].toString(),
                    token = token
                )
            )
        } catch (e: IllegalArgumentException) {
            return@transaction Result.failure(IllegalArgumentException("e"))
        }
    }


}