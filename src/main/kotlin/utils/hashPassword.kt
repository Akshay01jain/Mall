package com.sanmati.utils

import at.favre.lib.crypto.bcrypt.BCrypt

fun hashPassword(password: String): String =
    BCrypt.withDefaults().hashToString(
        12, password.toCharArray()
    )

