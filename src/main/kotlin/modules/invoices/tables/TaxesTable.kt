package com.sanmati.modules.invoices.tables

import org.jetbrains.exposed.sql.Table

object TaxesTable : Table("taxes_table") {
    val tax_id = integer("tax_id").autoIncrement()
    val name = varchar("name", 100)
    val rate = decimal("rate", 5, 2)
    val isActive = bool("is_active").default(true)

    override val primaryKey = PrimaryKey(tax_id)
}