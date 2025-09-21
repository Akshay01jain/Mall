package com.sanmati.modules.products.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object SubcategoryTable : Table("subcategory_table") {

    val subcategory_id = integer("subcategory_id").autoIncrement()
    val category_id = integer("category_id").references(CategoryTable.category_id) // FK to parent category
    val name = varchar("name", 255).uniqueIndex()
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)

    override val primaryKey = PrimaryKey(subcategory_id)
}
