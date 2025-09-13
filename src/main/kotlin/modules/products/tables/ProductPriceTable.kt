package com.sanmati.modules.products.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object ProductPricesTable : Table("product_prices_table") {
    val product_price_id = long("product_price_id").autoIncrement()
    val productId = long("product_id").references(ProductsTable.productId, onDelete = ReferenceOption.CASCADE)
    val salePrice = decimal("sale_price", 12, 2)
    val salePriceGstIncluded = bool("sale_price_gst_included").default(false)
    val saleDiscountPercent = decimal("sale_discount_percent", 5, 2).default(0.toBigDecimal())
    val wholesalePrice = decimal("wholesale_price", 12, 2).nullable()
    val wholesalePriceGstIncluded = bool("wholesale_price_gst_included").default(false)
    val productMRP = decimal(name = "product_MRP", precision = 12, 2).nullable()
    val minWholesaleQty = integer("min_wholesale_qty").default(0)
    val purchasePrice = decimal("purchase_price", 12, 2).nullable()
    val purchasePriceGstIncluded = bool("purchase_price_gst_included").default(false)
    val gstTaxPercentage = decimal("gst_tax_percentage", 5, 2).default(0.toBigDecimal())
    override val primaryKey = PrimaryKey(product_price_id)
}