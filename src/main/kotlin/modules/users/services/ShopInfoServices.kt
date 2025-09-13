package com.sanmati.modules.users.services

import com.sanmati.modules.users.dto.CreateShopInfoRequest
import com.sanmati.modules.users.dto.CreateShopInfoResponse
import com.sanmati.modules.users.tables.ShopInfoTable
import com.sanmati.modules.users.tables.UsersTable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

object ShopInfoServices {

    fun createShopInfo(request: CreateShopInfoRequest): Result<String> = transaction {
        try {
            val user = UsersTable.selectAll().where { UsersTable.userId eq request.userId }.singleOrNull()
                ?: return@transaction Result.failure(IllegalArgumentException("User not found"))

            val isActive = user[UsersTable.userActive]
            val isVerified = user[UsersTable.phoneVerified]
            val userTypeId = user[UsersTable.userTypeId]

            if (!isActive) {
                return@transaction Result.failure(IllegalStateException("User is not active"))
            }
            if (!isVerified) {
                return@transaction Result.failure(IllegalStateException("User phone not verified"))
            }

            val allowedTypes = listOf(1, 2, 3) // Assuming: 1=Admin, 2=Buyer, 3=Seller
            if (userTypeId !in allowedTypes) {
                return@transaction Result.failure(IllegalStateException("User type not allowed to create shop"))
            }

            val shopId = "shop-${System.currentTimeMillis()}"

            ShopInfoTable.insert {
                it[ShopInfoTable.shopId] = shopId
                it[ShopInfoTable.userId] = request.userId
                it[ShopInfoTable.shopName] = request.shopName
                it[ShopInfoTable.gstNumber] = request.gstNumber
                it[ShopInfoTable.shopImage] = request.shopImage
                it[ShopInfoTable.addressId] = request.addressId
                it[ShopInfoTable.createdAt] = LocalDateTime.now()
                it[ShopInfoTable.updatedAt] = LocalDateTime.now()
            }

            return@transaction Result.success("User Shop created Successfully")

        } catch (e: Exception) {
            return@transaction Result.failure(IllegalArgumentException(e))
        }
    }

}