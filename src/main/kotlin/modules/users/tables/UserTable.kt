package com.sanmati.modules.users.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object UsersTable : Table("users") {
    val userId = varchar("user_id", 25).uniqueIndex()
    var userTypeId = integer("user_type_id")
    var userTypeName = varchar("user_type_name", 30)
    var username = varchar("username", 100)
    var userMobileNumber = varchar("user_mobile_number", 15).uniqueIndex()
    var userPassword = varchar("user_password", 255)
    var userActive = bool("user_active").default(true)
    var phoneVerified = bool("phone_verified").default(false)
    var lastLoginAt = datetime("last_login_at").nullable()
    var isDeleted = bool("is_deleted").default(false)
    var createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    var updatedAt = datetime("updated_at").defaultExpression(CurrentDateTime).nullable()

    override val primaryKey = PrimaryKey(userId)
}