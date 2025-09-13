package com.sanmati.di

import com.sanmati.modules.invoices.invoiceRoutes
import com.sanmati.modules.products.productRoutes
import com.sanmati.modules.users.userTypeRoutes
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        userTypeRoutes()
        productRoutes()
        invoiceRoutes()
    }
}
