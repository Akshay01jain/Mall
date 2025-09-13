package com.sanmati.modules.products.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object ProductAttributesTable : Table("product_attributes_table")
{
    val attributes_id = long("attributes_id").autoIncrement()
    val productId = long("product_id").references(ProductsTable.productId, onDelete = ReferenceOption.CASCADE).nullable()
    val attributeName = varchar("attributeName", 512)
    val value = varchar("value", 512)
    override val primaryKey = PrimaryKey(attributes_id)
}