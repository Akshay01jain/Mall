package com.sanmati.modules.products.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object CategoryTable : Table("category_table") {

    val category_id = integer("category_id").autoIncrement()
    val parentId = integer("parent_id").nullable()
    val name = varchar("name", 255)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    override val primaryKey = PrimaryKey(category_id)

}