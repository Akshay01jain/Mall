package com.sanmati.utils

import at.favre.lib.crypto.bcrypt.BCrypt

fun verifyPassword(password: String, hashedPassword: String): Boolean {


    return BCrypt.verifyer()
        .verify(password.toCharArray(), hashedPassword.toCharArray())
        .verified
}