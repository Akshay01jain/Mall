package com.sanmati.modules.invoices.tables

import com.sanmati.modules.users.tables.UsersTable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object InvoiceTable : Table("invoice_table") {

    val invoice_id = long("invoice_id").autoIncrement()
    val invoice_number = long("invoice_number")
    val userId = reference("user_id", UsersTable.userId).uniqueIndex().nullable()
    val subtotal = decimal("subtotal", 10, 2)
    val extra_charges = decimal("extra_charges", 10, 2).nullable()
    val tax_amount = decimal("tax_amount", 10,4).nullable()
    val total_amount = decimal("total_amount", 10, 4)
    val notes = text("notes").nullable()
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val updatedAt = datetime("updated_at").defaultExpression(CurrentDateTime).nullable()


    override val primaryKey = PrimaryKey(invoice_id, invoice_number)
}