package com.sanmati.modules.users.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object UserAddressesTable : Table("user_addresses") {
    val ad_id = integer("ad_id").autoIncrement()
    val userId = varchar("user_id", 25).references(UsersTable.userId)
    val addressName = varchar("address_name", 100)
    val addressLine1 = varchar("address_line_1", 255)
    val addressLine2 = varchar("address_line_2", 255).nullable()
    val city = varchar("city", 100)
    val state = varchar("state", 100)
    val country = varchar("country", 100).default("India")
    val pin_code = varchar("pin_code", 10)
    val landmark = varchar("landmark", 150).nullable()
    val isDefault = bool("is_default").default(false)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val updatedAt = datetime("updated_at").defaultExpression(CurrentDateTime).nullable()

    override val primaryKey = PrimaryKey(ad_id)
}