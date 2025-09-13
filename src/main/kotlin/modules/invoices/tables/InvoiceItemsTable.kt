package com.sanmati.modules.invoices.tables

import org.jetbrains.exposed.sql.Table

object InvoiceItemsTable : Table("invoice_items_table") {

        val invoiceId = reference("invoice_id", InvoiceTable.invoice_id)
        val product_id = long("product_id")
        val description = varchar("description", 255).nullable() // optional description
        val quantity = decimal("quantity", 10, 2)
        val freeQuantity = decimal("free_quantity", 10, 2).default(0.00.toBigDecimal()).nullable()
        val unitPrice = decimal("unit_price", 10, 2)
        val discount_percentages = decimal("discount_percentages", 10, 2).default(0.00.toBigDecimal())
        val discount = decimal("discount", 10, 2).default(0.00.toBigDecimal())
        val taxId = reference("tax_id", TaxesTable.tax_id).nullable()
        val taxAmount = decimal("tax_amount", 10, 2).default(0.00.toBigDecimal())

        val total = decimal("total", 10, 2) // (quantity * unitPrice) - discount + taxAmount
}