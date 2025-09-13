package com.sanmati.modules.products.services

import com.sanmati.modules.products.dto.ProductAttributeResponse
import com.sanmati.modules.products.dto.ProductImageResponse
import com.sanmati.modules.products.dto.ProductPriceResponse
import com.sanmati.modules.products.dto.ProductRequest
import com.sanmati.modules.products.dto.ProductResponse
import com.sanmati.modules.products.dto.StockAttributesResponse
import com.sanmati.modules.products.dto.StockResponse
import com.sanmati.modules.products.tables.CategoryTable
import com.sanmati.modules.products.tables.ProductAttributesTable
import com.sanmati.modules.products.tables.ProductImageTable
import com.sanmati.modules.products.tables.ProductPricesTable
import com.sanmati.modules.products.tables.ProductsTable
import com.sanmati.modules.products.tables.StockAttributesTable
import com.sanmati.modules.products.tables.StockTable
import com.sanmati.modules.products.tables.UnitsTable
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.net.MalformedURLException
import java.net.URL

object ProductServices
{
    fun addProduct(request: ProductRequest): Result<String> = transaction {
        try {

            val validationErrors = mutableListOf<String>().apply {
                if (request.company_id <= 0) add("Invalid company ID")
                if (request.name.isBlank()) add("Product name cannot be empty")
                if (request.hsn_Code.isNullOrBlank()) add("HSN code cannot be empty")
                if (request.item_Code.isNullOrBlank()) add("Item code cannot be empty")


                request.prices.forEachIndexed { index, price ->
                    if (price.salePrice <= 0) add("Price at index $index: Sale price must be positive")
                    if (price.gstTaxPercentage < 0) add("Price at index $index: GST percentage cannot be negative")
                    price.wholesalePrice?.let { if (it <= 0) add("Price at index $index: Wholesale price must be positive if provided") }
                    price.productMRP?.let { if (it <= 0) add("Price at index $index: MRP must be positive if provided") }
                    price.purchasePrice?.let { if (it <= 0) add("Price at index $index: Purchase price must be positive if provided") }
                }


                request.attributes.forEachIndexed { index, attr ->
                    if (attr.attributeName.isBlank()) add("Attribute at index $index: Name cannot be empty")
                    if (attr.value.isBlank()) add("Attribute at index $index: Value cannot be empty")
                }

                request.images.forEachIndexed { index, image ->
                    if (image.imageUrl.isBlank()) add("Image at index $index: URL cannot be empty")
                    try {
                        URL(image.imageUrl) // Validate URL format
                    } catch (e: MalformedURLException) {
                        add("Image at index $index: Invalid URL format")
                    }
                }

            }

            if (validationErrors.isNotEmpty()) {
                return@transaction Result.failure(
                    IllegalArgumentException(validationErrors.joinToString("\n"))
                )
            }

            val exists = ProductsTable.selectAll().where {
                (ProductsTable.item_Code eq request.item_Code) or
                        (ProductsTable.name eq request.name)
            }.count() > 0

            if (exists) {
                return@transaction Result.failure(
                    IllegalArgumentException("Product with this name or item code already exists")
                )
            }

            request.category_id?.let { categoryId ->
                if (CategoryTable.selectAll().where { CategoryTable.category_id eq categoryId }.empty()) {
                    return@transaction Result.failure(
                        IllegalArgumentException("Specified category does not exist")
                    )
                }
            }


            request.unit_id?.let { unitId ->
                if (UnitsTable.selectAll().where { UnitsTable.unit_id eq unitId }.empty()) {
                    return@transaction Result.failure(
                        IllegalArgumentException("Specified unit does not exist")
                    )
                }
            }



            try {
                createProduct(request)
                return@transaction Result.success("Product added successfully")
            } catch (e: Exception) {
                return@transaction Result.failure(
                    IllegalArgumentException("Failed to create product: ${e.message}")
                )
            }

        } catch (e: Exception) {
            return@transaction Result.failure(
                when (e) {
                    is IllegalArgumentException -> e
                    else -> IllegalArgumentException("System error: ${e.message}")
                }
            )
        }
    }


    private fun createProduct(request : ProductRequest) {

        val _productId = ProductsTable.insert {
            it[company_id] = request.company_id
            it[category_id] = request.category_id
            it[name] = request.name
            it[unit_id] = request.unit_id
            it[hsn_Code] = request.hsn_Code
            it[item_Code] = request.item_Code
            it[description] = request.description
        }.get(ProductsTable.productId)

        request.attributes.forEach { attr ->
            ProductAttributesTable.insert {
                it[productId] = _productId
                it[this.attributeName] = attr.attributeName
                it[this.value] = attr.value
            }
        }

        request.prices.forEach { price ->
            ProductPricesTable.insert {
                it[this.productId] = _productId
                it[this.salePrice] = price.salePrice.toBigDecimal()
                it[this.salePriceGstIncluded] = price.salePriceGstIncluded
                it[this.saleDiscountPercent] = price.saleDiscountPercent.toBigDecimal()
                it[this.wholesalePrice] = price.wholesalePrice!!.toBigDecimal()
                it[this.wholesalePriceGstIncluded] = price.wholesalePriceGstIncluded
                it[this.productMRP] = price.productMRP!!.toBigDecimal()
                it[this.minWholesaleQty] = price.minWholesaleQty
                it[this.purchasePrice] = price.purchasePrice!!.toBigDecimal()
                it[this.purchasePriceGstIncluded] = price.purchasePriceGstIncluded
                it[this.gstTaxPercentage] = price.gstTaxPercentage.toBigDecimal()
            }

        }

        request.images.forEach { image ->
            ProductImageTable.insert {
                it[productId] = _productId
                it[this.imageUrl] = image.imageUrl
                it[this.isPrimary] = image.isPrimary
            }
        }

        request.stocks.forEach { stock ->
            val stockId = StockTable.insert {
                it[productId] = _productId
                it[current_stock] = stock.quantity.toBigDecimal()
                it[minStockLevel] = stock.minStockLevel?.toBigDecimal()
                it[notes] = stock.notes
            }.get(StockTable.stock_id)

            // Create stock attributes if any
            stock.attributes?.forEach { attr ->
                StockAttributesTable.insert {
                    it[stock_id] = stockId
                    it[attributeName] = attr.attributeName
                    it[value] = attr.value
                    attr.quantity?.let { qty -> it[quantity] = qty }
                }
            }
        }
    }

    fun getProductList(request: String?): Result<List<ProductResponse>> = transaction {
        try {
            val exists = if (!request.isNullOrBlank()) {
                (ProductsTable.name.lowerCase() like "%${request.lowercase()}%") or
                        (ProductsTable.item_Code.lowerCase() like "%${request.lowercase()}%") or
                        (ProductsTable.hsn_Code.lowerCase() like "%${request.lowercase()}%")
            } else {
                Op.TRUE
            }

            val filterProductList = ProductsTable.selectAll().where { exists }
                .map { productRow ->
                    ProductResponse(
                        product_id = productRow[ProductsTable.productId],
                        company_id = productRow[ProductsTable.company_id],
                        category_id = productRow[ProductsTable.category_id],
                        name = productRow[ProductsTable.name],
                        hsn_code = productRow[ProductsTable.hsn_Code],
                        item_code = productRow[ProductsTable.item_Code],
                        unit_id = productRow[ProductsTable.unit_id],
                        description = productRow[ProductsTable.description],
                        created_at = productRow[ProductsTable.createdAt].toString(),
                        updated_at = productRow[ProductsTable.updatedAt].toString(),
                        attributes = ProductAttributesTable.selectAll().where {
                            ProductAttributesTable.productId eq productRow[ProductsTable.productId]
                        }.map { attrRow ->
                            ProductAttributeResponse(
                                attribute_id = attrRow[ProductAttributesTable.attributes_id],
                                product_id = attrRow[ProductAttributesTable.productId]!!,
                                attribute_name = attrRow[ProductAttributesTable.attributeName],
                                value = attrRow[ProductAttributesTable.value]
                            )
                        },
                        prices = ProductPricesTable.selectAll().where {
                            ProductPricesTable.productId eq productRow[ProductsTable.productId]
                        }.map { priceRow ->
                            ProductPriceResponse(
                                price_id = priceRow[ProductPricesTable.product_price_id],
                                product_id = priceRow[ProductPricesTable.productId],
                                sale_price = priceRow[ProductPricesTable.salePrice].toDouble(),
                                sale_price_gst_included = priceRow[ProductPricesTable.salePriceGstIncluded],
                                sale_discount_percent = priceRow[ProductPricesTable.saleDiscountPercent].toDouble(),
                                wholesale_price = priceRow[ProductPricesTable.wholesalePrice]?.toDouble(),
                                wholesale_price_gst_included = priceRow[ProductPricesTable.wholesalePriceGstIncluded],
                                product_mrp = priceRow[ProductPricesTable.productMRP]?.toDouble(),
                                min_wholesale_qty = priceRow[ProductPricesTable.minWholesaleQty],
                                purchase_price = priceRow[ProductPricesTable.purchasePrice]?.toDouble(),
                                purchase_price_gst_included = priceRow[ProductPricesTable.purchasePriceGstIncluded],
                                gst_tax_percentage = priceRow[ProductPricesTable.gstTaxPercentage].toDouble()
                            )
                        },
                        images = ProductImageTable.selectAll().where {
                            ProductImageTable.productId eq productRow[ProductsTable.productId]
                        }.map { imageRow ->
                            ProductImageResponse(
                                image_id = imageRow[ProductImageTable.image_id],
                                product_id = imageRow[ProductImageTable.productId],
                                image_url = imageRow[ProductImageTable.imageUrl],
                                is_primary = imageRow[ProductImageTable.isPrimary]
                            )
                        },
                        stocks = StockTable.selectAll().where {
                            StockTable.productId eq productRow[ProductsTable.productId]
                        }.map { stockRow ->
                            StockResponse(
                                stockId = stockRow[StockTable.stock_id],
                                productId = stockRow[StockTable.productId],
                                currentStock = stockRow[StockTable.current_stock].toDouble(),
                                attributes = StockAttributesTable.selectAll().where {
                                    StockAttributesTable.stock_id eq stockRow[StockTable.stock_id]
                                }.map { attrRow ->
                                    StockAttributesResponse(
                                        stock_id = attrRow[StockAttributesTable.stock_id].toString(),
                                        attributeName = attrRow[StockAttributesTable.attributeName],
                                        value = attrRow[StockAttributesTable.value],
                                        quantity = attrRow[StockAttributesTable.quantity]
                                    )
                                },
                                updatedAt = stockRow[StockTable.updatedAt].toString()
                            )
                        }
                    )
                }

            return@transaction if (filterProductList.isNotEmpty()) {
                Result.success(filterProductList)
            } else {
                Result.failure(IllegalArgumentException("No products found matching criteria"))
            }

        } catch (e: Exception) {
            return@transaction Result.failure(
                e.takeIf { it is IllegalArgumentException }
                    ?: IllegalArgumentException("Error retrieving product list: ${e.message}")
            )
        }
    }

    fun deleteProduct(productId: Long): Result<String> = transaction {
        try {

            if (productId <= 0) {
                return@transaction Result.failure(
                    IllegalArgumentException("Invalid product ID")
                )
            }

            val productExists = ProductsTable.selectAll().where {
                ProductsTable.productId eq productId
            }.count() > 0

            if (!productExists) {
                return@transaction Result.failure(
                    IllegalArgumentException("Product not found")
                )
            }

/*
            val hasDependencies = OrderItemsTable.select {
                OrderItemsTable.productId eq productId
            }.count() > 0
*/

            /*if (hasDependencies) {
                return@transaction Result.failure(
                    IllegalArgumentException("Cannot delete product - it has associated orders")
                )
            }
*/

            try {
                // Delete prices first (child of product)
                ProductPricesTable.deleteWhere {
                    ProductPricesTable.productId eq productId
                }

                // Delete attributes
                ProductAttributesTable.deleteWhere {
                    ProductAttributesTable.productId eq productId
                }

                // Delete images
                ProductImageTable.deleteWhere {
                    ProductImageTable.productId eq productId
                }

                // Finally delete the product
                ProductsTable.deleteWhere {
                    ProductsTable.productId eq productId
                }

                return@transaction Result.success("Product deleted successfully")

            } catch (e: Exception) {
                rollback() // Explicit rollback on failure
                return@transaction Result.failure(
                    IllegalArgumentException("Failed to delete product: ${e.message}")
                )
            }

        } catch (e: Exception) {
            return@transaction Result.failure(
                when (e) {
                    is IllegalArgumentException -> e
                    else -> IllegalArgumentException("System error during deletion")
                }
            )
        }
    }
}