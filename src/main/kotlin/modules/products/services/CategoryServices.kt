package com.sanmati.modules.products.services

import com.sanmati.modules.products.dto.CategoryRequest
import com.sanmati.modules.products.dto.CategoryResponse
import com.sanmati.modules.products.tables.CategoryTable
import com.sanmati.modules.products.tables.SubcategoryTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
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
            if (request.name.isBlank()) {
                return@transaction Result.failure(IllegalArgumentException("Category name cannot be empty"))
            }

            // Check for duplicates (case-insensitive)
            val exists = CategoryTable.selectAll().where { CategoryTable.name.lowerCase() eq request.name.lowercase() }.count() > 0
            if (exists) return@transaction Result.failure(IllegalArgumentException("Category '${request.name}' already exists"))

            // Create category
            CategoryTable.insert {
                it[name] = request.name.trim()
                it[createdAt] = LocalDateTime.now()
            }

            Result.success("Category created successfully")
        } catch (e: Exception) {
            Result.failure(IllegalArgumentException("Failed to create category: ${e.message}"))
        }
    }

    fun getCategoryList(searchQuery: String?): Result<List<CategoryResponse>> = transaction {
        try {
            val query = if (!searchQuery.isNullOrBlank()) {
                val term = "%${searchQuery.lowercase()}%"
                CategoryTable.selectAll().where { CategoryTable.name.lowerCase() like term }
            } else CategoryTable.selectAll()

            val categories = query.map {
                CategoryResponse(
                    categoryId = it[CategoryTable.category_id],
                    name = it[CategoryTable.name],
                    createdAt = it[CategoryTable.createdAt].toString(),
                )
            }

            if (categories.isEmpty()) {
                Result.failure(IllegalArgumentException(if (searchQuery != null) "No categories match '$searchQuery'" else "No categories found"))
            } else Result.success(categories)

        } catch (e: Exception) {
            Result.failure(IllegalArgumentException("Failed to retrieve categories: ${e.message}"))
        }
    }

    fun deleteCategory(categoryId: Int): Result<String> = transaction {
        try {
            if (categoryId <= 0) return@transaction Result.failure(IllegalArgumentException("Invalid category ID"))

            // Check if category exists
            val categoryExists = CategoryTable.selectAll().where { CategoryTable.category_id eq categoryId }.count() > 0
            if (!categoryExists) return@transaction Result.failure(IllegalArgumentException("Category not found"))

            // Delete all subcategories first (cascading)
            SubcategoryTable.deleteWhere { SubcategoryTable.category_id eq categoryId }

            // Delete linked products if needed (optional, depending on your business logic)
            // ProductsTable.deleteWhere { ProductsTable.category_id eq categoryId }

            // Delete category
            CategoryTable.deleteWhere { CategoryTable.category_id eq categoryId }

            Result.success("Category and its subcategories deleted successfully")
        } catch (e: Exception) {
            Result.failure(IllegalArgumentException("Failed to delete category: ${e.message}"))
        }
    }
}
