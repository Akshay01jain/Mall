package com.sanmati.modules.users.services

import com.sanmati.modules.users.UserTypeList
import com.sanmati.modules.users.UserTypeTable
import com.sanmati.modules.users.dto.SendOTP
import com.sanmati.modules.users.dto.UserRequest
import com.sanmati.modules.users.dto.UserResponse
import com.sanmati.modules.users.tables.OtpVerifications
import com.sanmati.modules.users.tables.UsersTable
import com.sanmati.utils.hashPassword
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.Duration
import java.time.LocalDateTime
import kotlin.time.Clock

@OptIn(kotlin.time.ExperimentalTime::class)
object RegistrationServices {

    fun sendOTP(request: SendOTP): Result<String> = transaction {
        try {
            // Validate mobile number format
            if (request.userMobileNumber.isBlank() || !request.userMobileNumber.matches(Regex("^[0-9]{10}$"))) {
                return@transaction Result.failure(
                    IllegalArgumentException("Please enter a valid 10-digit mobile number")
                )
            }

            // Check if mobile number is already registered
            val exists = UsersTable.selectAll()
                .where { UsersTable.userMobileNumber eq request.userMobileNumber }
                .count() > 0

            if (exists) {
                return@transaction Result.failure(
                    IllegalArgumentException("Mobile number is already registered")
                )
            }

            val otp = "111111"
            val now = LocalDateTime.now()

            try {
                // Try to update existing OTP record
                val updated = OtpVerifications.update(
                    where = { OtpVerifications.mobileNumber eq request.userMobileNumber }
                ) {
                    it[otpCode] = otp
                    it[createdAt] = now
                    it[isUsed] = false
                }

                // If no record was updated, insert a new one
                if (updated == 0) {
                    OtpVerifications.insert {
                        it[mobileNumber] = request.userMobileNumber
                        it[otpCode] = otp
                        it[createdAt] = now
                        it[isUsed] = false
                    }
                }

                // In a real application, you would send the OTP via SMS here
                // smsService.sendOTP(request.userMobileNumber, otp)

                Result.success("OTP sent to ${request.userMobileNumber}")
            } catch (e: IllegalArgumentException) {
                Result.failure(IllegalArgumentException("Failed to generate OTP: ${e.message}"))
            }
        } catch (e: IllegalArgumentException) {
            Result.failure(IllegalArgumentException("OTP sending failed: ${e.message}"))
        }
    }

    //registration
    fun registration(request: UserRequest): Result<UserResponse> = transaction {

        try {
            if (request.userMobileNumber.isBlank() || request.otp.isBlank() || request.userTypeName.isBlank()) {
                return@transaction Result.failure(IllegalArgumentException("Required fields are missing"))
            }

            if(request.userMobileNumber.length != 10)
            {
                return@transaction Result.failure(IllegalArgumentException("Mobile Number is Invalid."))
            }

            val exists = UsersTable.selectAll().where { UsersTable.userMobileNumber eq request.userMobileNumber }.count() > 0

            if (exists) {
                return@transaction Result.failure(IllegalArgumentException("This mobile number is already exist."))
            }

            // Validate OTP
            val otpValidationResult = validateOtp(request.userMobileNumber, request.otp)
            if (otpValidationResult.isFailure) {
                return@transaction Result.failure(
                    otpValidationResult.exceptionOrNull() ?: IllegalArgumentException("OTP validation failed")
                )
            }

            // Mark OTP as used
            try {
                markOtpAsUsed(request.userMobileNumber)
            } catch (e: IllegalArgumentException) {
                return@transaction Result.failure(IllegalArgumentException("Failed to mark OTP as used"))
            }

            // Get user type
            val typeRow = UserTypeTable.selectAll().where { UserTypeTable.name eq request.userTypeName }.singleOrNull()
                ?: return@transaction Result.failure(IllegalArgumentException("Invalid user type"))

            val typeId = typeRow[UserTypeTable.id]
            val typeName = typeRow[UserTypeTable.name]

            // Generate user ID
            val userId = try {
                generateUserId(typeName)
            } catch (e: IllegalArgumentException) {
                return@transaction Result.failure(IllegalArgumentException("Failed to generate user ID"))
            }

            val now = Clock.System.now().toString()

            try {
                createUser(request, typeId, typeName, userId)
            } catch (e: IllegalArgumentException) {
                return@transaction Result.failure(IllegalArgumentException("Failed to create user: ${e.message}"))
            }

            // Return success response
            return@transaction Result.success(
                UserResponse(
                    userId = userId,
                    userTypeId = typeId,
                    userTypeName = typeName,
                    username = request.username,
                    userMobileNumber = request.userMobileNumber,
                    userActive = typeName.lowercase() == UserTypeList.CUSTOMER.toString().lowercase(),
                    phoneVerified = true,
                    lastLoginAt = now,
                    isDeleted = false,
                    createdAt = now,
                    updateAt = now
                )
            )
        } catch (e: IllegalArgumentException) {
            Result.failure(IllegalArgumentException("Registration failed: ${e.message}"))
        }
    }

    private fun validateOtp(mobileNumber: String, otpCode: String): Result<Unit> {
        return try {

            println("otpCode : $otpCode")
            println("otpVerification : ${OtpVerifications.otpCode}")

            val otpRow = OtpVerifications.selectAll().where {
                (OtpVerifications.mobileNumber eq mobileNumber) and
                        (OtpVerifications.otpCode eq otpCode) and
                        (OtpVerifications.isUsed eq false)
            }.singleOrNull() ?: return Result.failure(IllegalArgumentException("Invalid OTP"))


            val now = LocalDateTime.now()
            val createdAt = otpRow[OtpVerifications.createdAt]

            // Calculate duration between creation and now
            val duration = Duration.between(createdAt, now)
            val expirationMinutes = 5 // More reasonable expiration time

            if (duration.toMinutes() > expirationMinutes) {
                Result.failure(IllegalArgumentException("OTP expired (valid for $expirationMinutes minutes)"))
            } else {
                Result.success(Unit)
            }
        } catch (e: IllegalArgumentException) {
            Result.failure(IllegalArgumentException("OTP validation error: ${e.message}"))
        }
    }

    private fun markOtpAsUsed(mobileNumber: String) {
        try {
            OtpVerifications.update({ OtpVerifications.mobileNumber eq mobileNumber }) {
                it[isUsed] = true
            }
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Failed to update OTP status")
        }
    }

    private fun generateUserId(userTypeName: String): String {
        return try {

            val prefix = when (userTypeName.lowercase()) {
                UserTypeList.Admin.toString().lowercase() -> "adm"
                UserTypeList.Supplier.toString().lowercase() -> "sup"
                UserTypeList.RETAILER.toString().lowercase() -> "ret"
                else -> "cus"
            }

            val lastUserNumber = UsersTable.selectAll()
                .where { UsersTable.userTypeName eq userTypeName }
                .orderBy(UsersTable.createdAt, SortOrder.DESC)
                .limit(1)
                .singleOrNull()
                ?.get(UsersTable.userId)
                ?.split("-")
                ?.getOrNull(1)
                ?.toIntOrNull() ?: 0

            "$prefix-${"%04d".format(lastUserNumber + 1)}"

        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("User ID generation failed: ${e.message}")
        }
    }

    private fun createUser(request: UserRequest, type_id: Int, type_name: String, userId_: String) {
        try {
            UsersTable.insert {
                it[userId] = userId_
                it[userTypeId] = type_id
                it[userTypeName] = type_name
                it[username] = request.username
                it[userMobileNumber] = request.userMobileNumber
                it[userPassword] = hashPassword(request.userPassword)
                it[isDeleted] = false
                it[phoneVerified] = true
                it[userActive] = type_name.lowercase() == UserTypeList.CUSTOMER.toString().lowercase()
                it[createdAt] = LocalDateTime.now()
                it[updatedAt] = LocalDateTime.now()
            }
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("User creation failed: ${e.message}")
        }
    }


}
