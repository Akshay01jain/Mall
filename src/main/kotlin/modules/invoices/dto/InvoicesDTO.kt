package com.sanmati.modules.invoices.dto

import kotlinx.serialization.Serializable

@Serializable
data class InvoiceRequest(
    val invoice_number : String,
    val userId: String? = "",
    val subtotal: Double,
    val extra_charges: Double? = 0.0,
    val tax_amount: Double? = 0.0,
    val total_amount: Double,
    val notes: String? = null,
    val items: List<InvoiceItemRequest>
)
@Serializable
data class InvoiceItemRequest(
    val productId: Long? = null,
    val description: String? = null,
    val quantity: Double,
    val freeQuantity: Double? = 0.0,
    val unitPrice: Double,
    val discount_percentages: Double,
    val discountValue: Double? = 0.0,
    val taxId: Int? = 0,
    val taxAmount: Double,
    val total : Double
)

