package com.sanmati.modules.products.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object UnitsTable : Table("units_table") {
    val unit_id = integer("unit_id").autoIncrement()
    val unitName = varchar("unit_name", 100).uniqueIndex()
    val unitCode = varchar("unit_code", 20).nullable()
    val createdAt = datetime("created_at").clientDefault { LocalDateTime.now() }
    val updatedAt = datetime("updated_at").clientDefault { LocalDateTime.now() }

    override val primaryKey = PrimaryKey(unit_id)
}
