package com.sanmati.modules.products.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProductPriceRequest(
    val salePrice: Double,
    val salePriceGstIncluded: Boolean = false,
    val saleDiscountPercent: Double = 0.00,
    val wholesalePrice: Double? = null,
    val wholesalePriceGstIncluded: Boolean = false,
    val productMRP: Double? = null,
    val minWholesaleQty: Int = 0,
    val purchasePrice: Double? = null,
    val purchasePriceGstIncluded: Boolean = false,
    val gstTaxPercentage: Double = 0.00
)


@Serializable
data class ProductPriceResponse(
    val price_id: Long,
    val product_id: Long,
    val sale_price: Double,
    val sale_price_gst_included: Boolean,
    val sale_discount_percent: Double,
    val wholesale_price: Double?,
    val wholesale_price_gst_included: Boolean,
    val product_mrp: Double?,
    val min_wholesale_qty: Int,
    val purchase_price: Double?,
    val purchase_price_gst_included: Boolean,
    val gst_tax_percentage: Double
)