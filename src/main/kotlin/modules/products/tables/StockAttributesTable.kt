package com.sanmati.modules.products.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object StockAttributesTable : Table("stock_attribute_table")
{
    val attributes_id = long("attributes_id").autoIncrement()
    val stock_id = long("stock_id").references(StockTable.stock_id, onDelete = ReferenceOption.CASCADE).nullable()
    val attributeName = varchar("attributeName", 512)
    val value = varchar("value", 512)
    val quantity = long("quantity").nullable()
    override val primaryKey = PrimaryKey(attributes_id)
}