package com.sanmati.modules.products.services

import com.sanmati.modules.products.dto.UnitRequest
import com.sanmati.modules.products.dto.UnitResponse
import com.sanmati.modules.products.tables.UnitsTable
import com.sanmati.modules.users.tables.UserAddressesTable
import com.sanmati.modules.users.tables.UsersTable
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
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

            val exist = UnitsTable.selectAll().where { UnitsTable.unitName eq request.unitName }.count() > 0

            if (exist) {
                return@transaction Result.failure(IllegalArgumentException("This unit is available in the list."))
            } else {
                createUnit(request)

                return@transaction Result.success("Unit is added successfully.")
            }

        } catch (e: IllegalArgumentException) {
            return@transaction Result.failure(IllegalArgumentException(e))
        }

    }

    private fun createUnit(request: UnitRequest) {
        UnitsTable.insert {
            it[unitName] = request.unitName
            it[unitCode] = request.unitCode
            it[createdAt] = LocalDateTime.now()
            it[updatedAt] = LocalDateTime.now()
        }
    }

    fun getUnitList(request: String?): Result<List<UnitResponse>> = transaction {
        try {

            val exists = if (!request.isNullOrBlank() && request.length >= 2) {
                (UnitsTable.unitName.lowerCase() like "%${request.lowercase()}%") or
                        (UnitsTable.unitCode.lowerCase() like "%${request.lowercase()}%")
            } else {
                Op.TRUE
            }

            val filterUnitList = UnitsTable.selectAll().where { exists }
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
            val updated = UnitsTable.update({ UnitsTable.unit_id eq id }) {
                it[unitName] = request.unitName
                it[unitCode] = request.unitCode
                it[updatedAt] = LocalDateTime.now()
            }
            return@transaction if (updated > 0) Result.success("Unit updated successfully.")
            else Result.failure(Exception("Unit not found"))
        } catch (e: IllegalArgumentException) {
            return@transaction Result.failure(e)
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