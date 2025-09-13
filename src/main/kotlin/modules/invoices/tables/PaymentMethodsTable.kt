package com.sanmati.modules.invoices.tables

import org.jetbrains.exposed.sql.Table

object PaymentMethodsTable : Table("payment_methods") {

    val id = integer("id").autoIncrement()
    val name = varchar("name", 100) // e.g., Cash, UPI, Card
    val description = varchar("description", 255).nullable()
    val isActive = bool("is_active").default(true)
    override val primaryKey = PrimaryKey(id)
}