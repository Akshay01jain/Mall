package com.sanmati.modules.products.services

import com.sanmati.modules.products.dto.UnitRequest
import com.sanmati.modules.products.dto.UnitResponse
import com.sanmati.modules.products.tables.UnitsTable
import com.sanmati.modules.users.tables.UserAddressesTable
import com.sanmati.modules.users.tables.UsersTable
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime

object UnitServices {

    fun addUnit(request: UnitRequest): Result<String> = transaction {
        try {
            // Validate Unit Code
            if (request.unitCode.isNullOrBlank()) {
                return@transaction Result.failure(
                    IllegalArgumentException("Unit Code cannot be empty.")
                )
            }
            if (request.unitCode.length > 5) {
                return@transaction Result.failure(
                    IllegalArgumentException("Unit Code must not exceed 5 characters.")
                )
            }

            // Validate Unit Name
            if (request.unitName.isBlank()) {
                return@transaction Result.failure(
                    IllegalArgumentException("Unit Name cannot be empty.")
                )
            }

            // Trim values for consistency
            val cleanName = request.unitName.trim()
            val cleanCode = request.unitCode.trim()

            // ✅ Faster existence check (limit 1 → O(1))
            val exists = UnitsTable
                .selectAll()
                .where {
                    (UnitsTable.unitName.lowerCase() eq cleanName.lowercase()) or
                            (UnitsTable.unitCode.lowerCase() eq cleanCode.lowercase())
                }
                .limit(1)
                .any()   // true if any row exists

            if (exists) {
                return@transaction Result.failure(
                    IllegalArgumentException("This unit already exists.")
                )
            }

            // Insert new Unit with timestamps
            UnitsTable.insert {
                it[unitName] = cleanName
                it[unitCode] = cleanCode
                it[createdAt] = LocalDateTime.now()
                it[updatedAt] = LocalDateTime.now()
            }

            return@transaction Result.success("Unit added successfully.")

        } catch (e: IllegalArgumentException) {
            return@transaction Result.failure(
                IllegalArgumentException(e.message ?: "Invalid Unit data")
            )
        }
    }



    fun getUnitList(
        request: String?,
        page: Int = 1,
        pageSize: Int = 20
    ): Result<List<UnitResponse>> = transaction {
        try {
            val condition = if (!request.isNullOrBlank() && request.length >= 2) {
                (UnitsTable.unitName.lowerCase() like "%${request.lowercase()}%") or
                        (UnitsTable.unitCode.lowerCase() like "%${request.lowercase()}%")
            } else {
                Op.TRUE
            }

            val offset = (page - 1) * pageSize

            val filterUnitList = UnitsTable
                .selectAll()
                .where { condition }
                .orderBy(UnitsTable.unitName to SortOrder.ASC)   // Alphabetical order
                .limit(pageSize).offset(offset.toLong())               // Pagination
                .map {
                    UnitResponse(
                        unitId = it[UnitsTable.unit_id],
                        unitName = it[UnitsTable.unitName],
                        unitCode = it[UnitsTable.unitCode],
                        createdAt = it[UnitsTable.createdAt].toString(),
                        updatedAt = it[UnitsTable.updatedAt].toString()
                    )
                }

            return@transaction if (filterUnitList.isNotEmpty()) {
                Result.success(filterUnitList)
            } else {
                Result.failure(IllegalArgumentException("No Unit found."))
            }
        } catch (e: IllegalArgumentException) {
            return@transaction Result.failure(IllegalArgumentException(e))
        }
    }


    fun updateUnit(id: Int, request: UnitRequest): Result<String> = transaction {
        try {
            // Check for duplicate name/code excluding current id
            val exists = UnitsTable
                .selectAll().where {
                    ((UnitsTable.unitName.lowerCase() eq request.unitName.lowercase()) or
                            (UnitsTable.unitCode.lowerCase() eq request.unitCode.lowercase())) and
                            (UnitsTable.unit_id neq id)
                }
                .limit(1)
                .any()

            if (exists) return@transaction Result.failure(Exception("Unit name or code already exists"))

            val updated = UnitsTable.update({ UnitsTable.unit_id eq id }) {
                it[unitName] = request.unitName
                it[unitCode] = request.unitCode
                it[updatedAt] = LocalDateTime.now()
            }

            if (updated > 0) Result.success("Unit updated successfully.")
            else Result.failure(Exception("Unit not found"))

        } catch (e: Exception) {
            Result.failure(Exception("Failed to update unit: ${e.message}"))
        }
    }

    fun deleteUnit(id: Int): Result<String> = transaction {

        try {
            val exist = UnitsTable.selectAll().where { UnitsTable.unit_id eq id }

            if (exist.firstOrNull() != null) {

                UnitsTable.deleteWhere { UnitsTable.unit_id eq id }
                return@transaction Result.success("Unit deleted successfully.")

            } else {
                return@transaction Result.failure(IllegalArgumentException("No unit found."))
            }

        }catch (e : IllegalArgumentException)
        {
            return@transaction Result.failure(e)
        }
    }
}