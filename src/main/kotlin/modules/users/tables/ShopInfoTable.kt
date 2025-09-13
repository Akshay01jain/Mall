package com.sanmati.modules.users.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object ShopInfoTable : Table("shop_info") {

    val shopId = varchar("shop_id", 20).uniqueIndex() // e.g., shop-0001
    val userId = reference("user_id", UsersTable.userId).uniqueIndex()
    val shopName = varchar("shop_name", 100)
    val gstNumber = varchar("gst_number", 15).nullable()
    val shopImage = varchar("shop_image", 255).nullable()
    val addressId = reference("address_id", UserAddressesTable.ad_id).nullable()
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val updatedAt = datetime("updated_at").defaultExpression(CurrentDateTime).nullable()

    override val primaryKey = PrimaryKey(shopId)
}
