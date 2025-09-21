package com.sanmati.modules.products

import com.sanmati.modules.products.controller.CategoryController
import com.sanmati.modules.products.controller.CompanyController
import com.sanmati.modules.products.controller.ProductController
import com.sanmati.modules.products.controller.SubcategoryController
import com.sanmati.modules.products.controller.UnitController
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

        route("/category") {
            // Add Category
            post {
                CategoryController.addCategory(call)
            }

            // Get Categories (optionally with search)
            get {
                CategoryController.getCategoryList(call)
            }

            // Delete Category (and its subcategories)
            delete("/delete") {
                CategoryController.deleteCategory(call)
            }
        }

        route("/subcategory") {
            // Add Subcategory
            post {
                SubcategoryController.addSubcategory(call)
            }

            get {
                SubcategoryController.getSubcategories(call)
            }

            // Delete Subcategory
            delete("/delete") {
                SubcategoryController.deleteSubcategory(call)
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