package com.sanmati.modules.products.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object CompanyTable : Table("company_table")
{
    val company_id = integer("company_id").autoIncrement()
    val name = varchar("name", 255)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)

    override val primaryKey = PrimaryKey(company_id)

}