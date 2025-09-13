package com.sanmati.modules.invoices.services

import com.sanmati.modules.invoices.dto.CreateTaxRequest
import com.sanmati.modules.invoices.dto.TaxResponse
import com.sanmati.modules.invoices.tables.TaxesTable
import com.sanmati.modules.products.dto.CompanyResponse
import com.sanmati.modules.products.tables.CompanyTable
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.contracts.Returns
import kotlin.toBigDecimal

object TaxServices {

    fun createTax(request: CreateTaxRequest): Result<String> = transaction {

        try {
            val exists = TaxesTable.selectAll().where{ TaxesTable.name eq request.name}.count() > 0

            if (!exists)
            {
                val taxId = TaxesTable.insert {
                    it[name] = request.name
                    it[rate] = request.rate.toBigDecimal()
                    it[isActive] = request.isActive
                }.get(TaxesTable.tax_id)

                TaxesTable.selectAll().where { TaxesTable.tax_id eq taxId }.single()

                return@transaction Result.success("Tax created successfully.")
            }
            else{
                return@transaction Result.failure(IllegalArgumentException("Tax is already created."))
            }
        }catch (e : IllegalArgumentException) {
            return@transaction Result.failure(e)
        }

    }


    fun getTaxList(request : String?) : Result<List<TaxResponse>> = transaction {
        try {
            val exists = if(!request.isNullOrBlank() && request.isNotEmpty())
            {
                (TaxesTable.name.lowerCase() like "%${request.lowercase()}%")
            }else{
                Op.TRUE
            }

            val filterTaxList = TaxesTable.selectAll().where { exists }
                .map {
                    TaxResponse(
                        taxId = it[TaxesTable.tax_id],
                        name = it[TaxesTable.name],
                        rate = it[TaxesTable.rate].toDouble(),
                        isActive = it[TaxesTable.isActive]

                    )
                }

            return@transaction if (filterTaxList.isNotEmpty()) {
                Result.success(filterTaxList)
            }else{
                Result.failure(IllegalArgumentException("No Tax found."))
            }

        }catch (e : IllegalArgumentException)
        {
            return@transaction Result.failure(e)
        }
    }
}