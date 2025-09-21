package com.sanmati.modules.products.services

import com.sanmati.modules.products.dto.SubcategoryRequest
import com.sanmati.modules.products.dto.SubcategoryResponse
import com.sanmati.modules.products.tables.CategoryTable
import com.sanmati.modules.products.tables.SubcategoryTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

object SubcategoryServices {

    fun addSubcategory(request: SubcategoryRequest): Result<String> = transaction {
        try {
            if (request.name.isBlank()) return@transaction Result.failure(IllegalArgumentException("Subcategory name cannot be empty"))

            // Check if parent_id category exists
            val categoryExists = CategoryTable.selectAll().where { CategoryTable.category_id eq request.categoryId }.count() > 0
            if (!categoryExists) return@transaction Result.failure(IllegalArgumentException("Parent category does not exist"))

            // Check for duplicates under the same category
            val exists = SubcategoryTable.selectAll().where {
                (SubcategoryTable.category_id eq request.categoryId) and
                        (SubcategoryTable.name.lowerCase() eq request.name.lowercase())
            }.count() > 0
            if (exists) return@transaction Result.failure(IllegalArgumentException("Subcategory '${request.name}' already exists under this category"))

            // Insert subcategory
            SubcategoryTable.insert {
                it[category_id] = request.categoryId
                it[name] = request.name.trim()
                it[createdAt] = LocalDateTime.now()
            }

            Result.success("Subcategory created successfully")
        } catch (e: Exception) {
            Result.failure(IllegalArgumentException("Failed to create subcategory: ${e.message}"))
        }
    }

    fun getSubcategoriesByCategory(categoryId: Int): Result<List<SubcategoryResponse>> = transaction {
        try {
            val subcategories = SubcategoryTable.selectAll().where { SubcategoryTable.category_id eq categoryId }
                .map {
                    SubcategoryResponse(
                        subcategoryId = it[SubcategoryTable.subcategory_id],
                        categoryId = it[SubcategoryTable.category_id],
                        name = it[SubcategoryTable.name],
                        createdAt = it[SubcategoryTable.createdAt].toString()
                    )
                }

            if (subcategories.isEmpty()) {
                Result.failure(IllegalArgumentException("No subcategories found for this category"))
            } else Result.success(subcategories)

        } catch (e: Exception) {
            Result.failure(IllegalArgumentException("Failed to retrieve subcategories: ${e.message}"))
        }
    }

    fun deleteSubcategory(subcategoryId: Int): Result<String> = transaction {
        try {
            val exists = SubcategoryTable.selectAll().where { SubcategoryTable.subcategory_id eq subcategoryId }.count() > 0
            if (!exists) return@transaction Result.failure(IllegalArgumentException("Subcategory not found"))

            SubcategoryTable.deleteWhere { SubcategoryTable.subcategory_id eq subcategoryId }
            Result.success("Subcategory deleted successfully")
        } catch (e: Exception) {
            Result.failure(IllegalArgumentException("Failed to delete subcategory: ${e.message}"))
        }
    }
}
