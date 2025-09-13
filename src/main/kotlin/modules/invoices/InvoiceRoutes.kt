package com.sanmati.modules.invoices

import com.sanmati.modules.invoices.controllers.TaxControllers
import com.sanmati.modules.products.controller.UnitController
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route

fun Route.invoiceRoutes()
{
    authenticate("auth-jwt") {
        //tax
        route("/taxes") {
            post {
                TaxControllers.addTax(call)
            }
            get {
                TaxControllers.getTaxList(call)
            }
        }
    }
}