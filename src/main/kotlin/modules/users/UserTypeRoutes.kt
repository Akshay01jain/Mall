package com.sanmati.modules.users

import com.sanmati.modules.users.controller.LoginController
import com.sanmati.modules.users.controller.RegistrationController
import com.sanmati.modules.users.controller.ShopController
import com.sanmati.modules.users.controller.UserAddressController
import com.sanmati.modules.users.controller.UserTypeController
import com.sanmati.modules.users.services.ShopInfoServices
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.userTypeRoutes() {

    authenticate("auth-jwt") {
        route("/user-type") {
            get {
                UserTypeController.getAllUserType(call)
            }

            get("/search") {
                UserTypeController.getUserTypeName(call)
            }

            post("/create") {
                UserTypeController.create(call)
            }
            delete("/delete")
            {
                UserTypeController.deleteUserType(call)
            }
        }

        route("/user-address") {

            post("/create") {
                UserAddressController.addAddress(call)
            }
            get {
                UserAddressController.addressList(call)
            }
            delete("/delete") {
                UserAddressController.deleteUserAddress(call)
            }
        }
    }

    route("/user/auth")
    {
        post("/sendOTP") {
            RegistrationController.sendOTP(call)
        }

        post("/registration") {
            RegistrationController.registerUser(call)
        }

        post("/shop_info/create") {
            ShopController.createShopInfo(call)
        }

        post("address/create") {
            UserAddressController.addAddress(call)
        }

        post("/login")
        {
            LoginController.loginUser(call)
        }
    }
}