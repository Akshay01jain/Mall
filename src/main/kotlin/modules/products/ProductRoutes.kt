package com.sanmati.modules.products

import com.sanmati.modules.products.controller.CompanyController
import com.sanmati.modules.products.controller.ProductController
import com.sanmati.modules.products.controller.UnitController
import com.sanmati.modules.products.controller.modules.products.controller.CategoryController
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route

fun Route.productRoutes() {

    authenticate("auth-jwt") {
        //unit
        route("/units") {
            post {
                UnitController.addUnit(call)
            }
            get {
                UnitController.getUnitList(call)
            }
            put("/update") {
                UnitController.updateUnit(call)
            }
            delete("/delete") {
                UnitController.deleteUnit(call)
            }
        }

        route("/company")
        {
            post {
                CompanyController.addCompany(call)
            }
            get {
                CompanyController.getCompanyList(call)
            }
            delete("/delete") {
                CompanyController.deleteCompany(call)
            }
        }

        route("/category")
        {
            post {
                CategoryController.addUnit(call)
            }
            get {
                CategoryController.getCategoryList(call)
            }
            delete("/delete") {
                CategoryController.deleteCategory(call)
            }
        }

        route("/product")
        {
            post {
                ProductController.addProduct(call)
            }
            get {
                ProductController.getProductList(call)
            }
            delete("/delete") {
                ProductController.deleteProduct(call)
            }
        }
    }


}