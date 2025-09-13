package com.sanmati.modules.products.services

object StockServices {

   /* fun updateProductStock(request: StockUpdateRequest): Result<String> = transaction {
        try {
            // Validate input
            if (request.productId <= 0) {
                return@transaction Result.failure(IllegalArgumentException("Invalid product ID"))
            }

            // Get or create stock record
            val stockRecord = StockTable.select {
                StockTable.productId eq request.productId
            }.firstOrNull() ?: run {
                StockTable.insertAndGetId {
                    it[productId] = request.productId
                    it[current_stock] = 0.0.toBigDecimal()
                }.let { id ->
                    StockTable.select { StockTable.stock_id eq id }.single()
                }
            }

            // Calculate new stock
            val currentStock = stockRecord[StockTable.current_stock]
            val newStock = when (request.movementType) {
                "PURCHASE" -> currentStock + request.quantity.toBigDecimal()
                "SALE" -> currentStock - request.quantity.toBigDecimal()
                "ADJUSTMENT" -> request.quantity.toBigDecimal()
                else -> currentStock
            }

            // Prevent negative stock
            if (newStock < BigDecimal.ZERO && request.movementType != "ADJUSTMENT") {
                return@transaction Result.failure(IllegalArgumentException("Insufficient stock available"))
            }

            // Update stock
            StockTable.update({ StockTable.stock_id eq stockRecord[StockTable.stock_id] }) {
                it[current_stock] = newStock
                it[updatedAt] = LocalDateTime.now()
                request.notes?.let { notes -> it[notes] = notes }
                request.minStockLevel?.let { level -> it[minStockLevel] = level.toBigDecimal() }
            }

            // Update/create attributes
            request.attributes?.forEach { attr ->
                if (attr.attributeName.isNotBlank() && attr.value.isNotBlank()) {
                    StockAttributesTable.upsert {
                        it[stock_id] = stockRecord[StockTable.stock_id]
                        it[attributeName] = attr.attributeName
                        it[value] = attr.value
                        attr.quantity?.let { qty -> it[quantity] = qty }
                    }
                }
            }

            Result.success("Stock updated successfully")
        } catch (e: Exception) {
            Result.failure(
                when (e) {
                    is IllegalArgumentException -> e
                    else -> IllegalArgumentException("Failed to update stock: ${e.message}")
                }
            )
        }
    }*/
}