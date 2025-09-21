package com.sanmati.db

import com.sanmati.modules.invoices.tables.TaxesTable
import com.sanmati.modules.products.tables.CategoryTable
import com.sanmati.modules.products.tables.CompanyTable
import com.sanmati.modules.products.tables.ProductAttributesTable
import com.sanmati.modules.products.tables.ProductImageTable
import com.sanmati.modules.products.tables.ProductPricesTable
import com.sanmati.modules.products.tables.ProductsTable
import com.sanmati.modules.products.tables.StockAttributesTable
import com.sanmati.modules.products.tables.StockTable
import com.sanmati.modules.products.tables.SubcategoryTable
import com.sanmati.modules.products.tables.UnitsTable
import com.sanmati.modules.users.UserTypeTable
import com.sanmati.modules.users.tables.OtpVerifications
import com.sanmati.modules.users.tables.ShopInfoTable
import com.sanmati.modules.users.tables.UserAddressesTable
import com.sanmati.modules.users.tables.UsersTable
import io.ktor.server.application.Application
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction


fun Application.initDB() {
    val dbUrl = environment.config.property("postgres.url").getString()
    val dbUser = environment.config.property("postgres.user").getString()
    val dbDriver = environment.config.property("postgres.driver").getString()
    val dbPassword = environment.config.property("postgres.password").getString()


    val postgresDB = Database.connect(
        url = dbUrl,
        user = dbUser,
        password = dbPassword,
        driver = dbDriver,
    )

    transaction(postgresDB)
    {
        SchemaUtils.create(
            UserTypeTable,
            UsersTable,
            OtpVerifications,
            UserAddressesTable,
            UnitsTable,
            CategoryTable,
            SubcategoryTable,
            CompanyTable,
            ProductsTable,
            ProductAttributesTable,
            ProductPricesTable,
            ProductImageTable,
            StockTable,
            StockAttributesTable,
            TaxesTable


        )
    }

}