package com.sanmati.modules.products.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object ProductsTable : Table("products_table") {

    val productId = long("product_id").autoIncrement()
    val company_id = integer("company_id").references(CompanyTable.company_id)
    val category_id = integer("category_id").references(CategoryTable.category_id).nullable()
    val unit_id = integer("unit_id").references(UnitsTable.unit_id).nullable()
    val name = varchar("name", 512)
    val hsn_Code = varchar("hsn_code", 64).nullable()
    val item_Code = varchar("item_code", 64).nullable().uniqueIndex()
    val description = text("description").nullable()
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val updatedAt = datetime("updated_at").defaultExpression(CurrentDateTime)

    override val primaryKey = PrimaryKey(productId)

}