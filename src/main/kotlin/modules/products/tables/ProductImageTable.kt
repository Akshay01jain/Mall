package com.sanmati.modules.products.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object ProductImageTable : Table("product_image_table")
{
    val image_id = long("image_id").autoIncrement()
    val productId = long("product_id").references(ProductsTable.productId, onDelete = ReferenceOption.CASCADE)
    val imageUrl = varchar("image_url", 1000)
    val isPrimary = bool("is_primary").default(false)
    override val primaryKey = PrimaryKey(image_id)
}