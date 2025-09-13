package com.sanmati.modules.products.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object StockTable : Table("stock_table") {
    val stock_id = long("stock_id").autoIncrement()
    val productId = long("product_id").references(ProductsTable.productId, onDelete = ReferenceOption.CASCADE)
    val current_stock = decimal("current_stock", 12, 3).default(0.0.toBigDecimal())
    val minStockLevel = decimal("min_stock_level", 12, 3).nullable()
    val notes = varchar("notes", 255).nullable()
    val updatedAt = datetime("updated_at").defaultExpression(CurrentDateTime)
    override val primaryKey = PrimaryKey(stock_id)
    init {
        uniqueIndex(productId)
    }
}
