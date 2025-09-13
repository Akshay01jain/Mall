package com.sanmati.modules.products.dto

import kotlinx.serialization.Serializable

@Serializable
data class StockAttributesRequest(
    val stock_id : String,
    val attributeName : String,
    val value : String,
    val quantity : Long? = 0
)

@Serializable
data class StockAttributesResponse(
    val stock_id : String,
    val attributeName : String,
    val value : String,
    val quantity : Long?
)