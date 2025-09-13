package com.sanmati.modules.products.services

import com.sanmati.modules.products.dto.CompanyRequest
import com.sanmati.modules.products.dto.CompanyResponse
import com.sanmati.modules.products.tables.CompanyTable
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

object CompanyServices {

    fun addCompany(request: CompanyRequest) : Result<String> = transaction {

        try {

            val exists = CompanyTable.selectAll().where {
                CompanyTable.name eq request.name
            }.count() > 0

            if(!exists)
            {
                CompanyTable.insert {
                    it[name] = request.name
                    it[createdAt] = LocalDateTime.now()
                }

                return@transaction Result.success("Company created successfully.")

            }else{
                return@transaction Result.failure(IllegalArgumentException("This company is already created."))
            }


        }catch (e : IllegalArgumentException)
        {
            return@transaction Result.failure(e)
        }

    }

    fun getCompanyList(request : String?) : Result<List<CompanyResponse>> = transaction {
        try {
            val exists = if(!request.isNullOrBlank() && request.isNotEmpty())
            {
                (CompanyTable.name.lowerCase() like "%${request.lowercase()}%")
            }else{
                Op.TRUE
            }

            val filterCompanyList = CompanyTable.selectAll().where { exists }
                .map {
                    CompanyResponse(
                        company_id = it[CompanyTable.company_id],
                        name = it[CompanyTable.name],
                        createdAt = it[CompanyTable.createdAt].toString()
                    )
                }

            return@transaction if (filterCompanyList.isNotEmpty()) {
                Result.success(filterCompanyList)
            }else{
                Result.failure(IllegalArgumentException("No Company found."))
            }

        }catch (e : IllegalArgumentException)
        {
            return@transaction Result.failure(e)
        }
    }

    fun deleteCompany(company_id: Int): Result<String> = transaction {

        try {
            val exist = CompanyTable.selectAll().where { CompanyTable.company_id eq company_id }

            if (exist.firstOrNull() != null) {

                CompanyTable.deleteWhere { CompanyTable.company_id eq company_id }
                return@transaction Result.success("Company deleted successfully.")

            } else {
                return@transaction Result.failure(IllegalArgumentException("No company found."))
            }

        }catch (e : IllegalArgumentException)
        {
            return@transaction Result.failure(e)
        }
    }

}