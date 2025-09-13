package com.sanmati.modules.users.services

import com.sanmati.modules.users.dto.AddAddressRequest
import com.sanmati.modules.users.dto.AddressRequest
import com.sanmati.modules.users.dto.DeleteAddressRequest
import com.sanmati.modules.users.dto.UserAddressResponse
import com.sanmati.modules.users.tables.UserAddressesTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime

object UserAddressServices {

    fun addAddress(request: AddAddressRequest): Result<String> = transaction {

        try {
            if (request.isDefault) {
                UserAddressesTable.update({ UserAddressesTable.userId eq request.userId }) {
                    it[isDefault] = false
                }
            }

            UserAddressesTable.insert { row ->
                row[userId] = request.userId
                row[addressName] = request.addressName!!
                row[addressLine1] = request.addressLine1
                row[addressLine2] = request.addressLine2
                row[city] = request.city
                row[state] = request.state
                row[country] = request.country
                row[pin_code] = request.pin_code
                row[landmark] = request.landmark
                row[isDefault] = request.isDefault
                row[createdAt] = LocalDateTime.now()
                row[updatedAt] = LocalDateTime.now()
            }

            return@transaction Result.success("Address Added Successfully. ")

        } catch (e: IllegalArgumentException) {
            return@transaction Result.failure(IllegalArgumentException(e))
        }
    }

    fun addressList(request: AddressRequest): Result<List<UserAddressResponse>> = transaction {

        try {

            val exist = if (request.userId != null) {
                UserAddressesTable.selectAll().where { UserAddressesTable.userId eq request.userId }
            } else {
                UserAddressesTable.selectAll()
            }

            val addresses = exist.map {
                UserAddressResponse(
                    ad_id = it[UserAddressesTable.ad_id],
                    userId = it[UserAddressesTable.userId],
                    addressName = it[UserAddressesTable.addressName],
                    addressLine1 = it[UserAddressesTable.addressLine1],
                    addressLine2 = it[UserAddressesTable.addressLine2],
                    city = it[UserAddressesTable.city],
                    state = it[UserAddressesTable.state],
                    country = it[UserAddressesTable.country],
                    pin_code = it[UserAddressesTable.pin_code],
                    landmark = it[UserAddressesTable.landmark],
                    isDefault = it[UserAddressesTable.isDefault]
                )
            }

            return@transaction if (addresses.isNotEmpty()) {
                Result.success(addresses)
            } else {
                Result.failure(IllegalArgumentException("No address found for this user"))
            }


        } catch (e: IllegalArgumentException) {
            return@transaction Result.failure(java.lang.IllegalArgumentException(e))
        }
    }


    fun deleteUserAddress(request: DeleteAddressRequest): Result<String> = transaction {

        try {
            val exist = UserAddressesTable.selectAll()
                .where { (UserAddressesTable.userId eq request.userId) and (UserAddressesTable.ad_id eq request.ad_id) }


            if (exist.firstOrNull() != null)
            {
                UserAddressesTable.deleteWhere{
                    (UserAddressesTable.userId eq request.userId) and (UserAddressesTable.ad_id eq request.ad_id )
                }

                return@transaction Result.success("Address deleted successfully.")
            }
            else {
                return@transaction Result.success("No address found for this user.")
            }

        }catch (e : IllegalArgumentException)
        {
            return@transaction Result.failure(IllegalArgumentException(e))
        }

    }

}