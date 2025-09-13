package com.sanmati.modules.invoices.services

import com.sanmati.modules.invoices.dto.InvoiceRequest
import com.sanmati.modules.users.tables.UsersTable
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate


object InvoiceService {

    /*fun createInvoice(request: InvoiceRequest) : Result<String> = transaction {
      *//*try {

          val exists = UsersTable.selectAll().where {
              UsersTable.userId eq request.userId
          }.count() > 0

          if(exists )


      } catch (e : IllegalArgumentException)
      {
          return@transaction Result.failure(e)
      }*//*
    }*/

}