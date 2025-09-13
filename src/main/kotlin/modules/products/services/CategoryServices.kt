package com.sanmati.modules.products.services

import com.sanmati.modules.products.dto.CategoryRequest
import com.sanmati.modules.products.dto.CategoryResponse
import com.sanmati.modules.products.tables.CategoryTable
import com.sanmati.modules.products.tables.ProductsTable
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

object CategoryServices {

    fun addCategory(request: CategoryRequest): Result<String> = transaction {
        try {
            // Input validation
            val validationErrors = mutableListOf<String>().apply {
                if (request.name.isBlank()) add("Category name cannot be empty")
                request.parentId?.let {
                    if (it <= 0) add("Invalid parent category ID")
                }
            }

            if (validationErrors.isNotEmpty()) {
                return@transaction Result.failure(
                    IllegalArgumentException(validationErrors.joinToString("\n"))
                )
            }

            // Check for duplicates (case-insensitive)
            val exists = CategoryTable.selectAll().where {
                CategoryTable.name.lowerCase() eq request.name.lowercase()
            }.count() > 0

            if (exists) {
                return@transaction Result.failure(
                    IllegalArgumentException("Category '${request.name}' already exists")
                )
            }

            // Validate parent category exists if provided
            request.parentId?.let { parentId ->
                if (CategoryTable.selectAll().where { CategoryTable.category_id eq parentId }.empty()) {
                    return@transaction Result.failure(
                        IllegalArgumentException("Parent category does not exist")
                    )
                }
            }

            // Create category
            CategoryTable.insert {
                it[name] = request.name.trim()
                it[parentId] = request.parentId
                it[createdAt] = LocalDateTime.now()
            }

            return@transaction Result.success("Category created successfully")

        } catch (e: Exception) {
            return@transaction Result.failure(
                when (e) {
                    is IllegalArgumentException -> e
                    else -> IllegalArgumentException("Failed to create category: ${e.message}")
                }
            )
        }
    }

    fun getCategoryList(searchQuery: String?): Result<List<CategoryResponse>> = transaction {
        try {
            val query = when {
                !searchQuery.isNullOrBlank() -> {
                    val searchTerm = "%${searchQuery.lowercase()}%"
                    CategoryTable.selectAll().where {
                        CategoryTable.name.lowerCase() like searchTerm
                    }
                }
                else -> CategoryTable.selectAll()
            }

            val categories = query.map {
                CategoryResponse(
                    categoryId = it[CategoryTable.category_id],
                    name = it[CategoryTable.name],
                    parentId = it[CategoryTable.parentId],
                    createdAt = it[CategoryTable.createdAt].toString(),

                )
            }

            return@transaction if (categories.isNotEmpty()) {
                Result.success(categories)
            } else {
                Result.failure(IllegalArgumentException(
                    if (searchQuery != null) "No categories match '$searchQuery'"
                    else "No categories found"
                ))
            }

        } catch (e: Exception) {
            return@transaction Result.failure(
                IllegalArgumentException("Failed to retrieve categories: ${e.message}")
            )
        }
    }

    fun deleteCategory(categoryId: Int): Result<String> = transaction {
        try {
            // Validate category ID
            if (categoryId <= 0) {
                return@transaction Result.failure(
                    IllegalArgumentException("Invalid category ID")
                )
            }

            // Check if category exists
            val category = CategoryTable.selectAll().where {
                CategoryTable.category_id eq categoryId
            }.firstOrNull()
                ?: return@transaction Result.failure(
                    IllegalArgumentException("Category not found")
                )

            // Check for subcategories
            val hasChildren = CategoryTable.selectAll().where {
                CategoryTable.parentId eq categoryId
            }.count() > 0

            if (hasChildren) {
                return@transaction Result.failure(
                    IllegalArgumentException("Cannot delete category - it has subcategories")
                )
            }

            // Check for linked products
            val hasProducts = ProductsTable.selectAll().where {
                ProductsTable.category_id eq categoryId
            }.count() > 0

            if (hasProducts) {
                return@transaction Result.failure(
                    IllegalArgumentException("Cannot delete category - it contains products")
                )
            }

            // Perform deletion
            CategoryTable.deleteWhere {
                CategoryTable.category_id eq categoryId
            }

            return@transaction Result.success("Category deleted successfully")

        } catch (e: Exception) {
            return@transaction Result.failure(
                when (e) {
                    is IllegalArgumentException -> e
                    else -> IllegalArgumentException("Failed to delete category: ${e.message}")
                }
            )
        }
    }
}