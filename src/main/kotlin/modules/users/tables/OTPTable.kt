package com.sanmati.modules.users.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object OtpVerifications : Table("otp_verifications") {
    val mobileNumber = varchar("mobile_number", 15).uniqueIndex()
    val otpCode = varchar("otp_code", 6)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val isUsed = bool("is_used").default(false)
}