package com.mkroo.lmsdemo.domain

import org.springframework.stereotype.Service

@Service
class PasswordValidator {
    fun isValid(password: String): Boolean {
        if (password.length < 6 || password.length > 10) return false

        val hasLowercase = password.any(Char::isLowerCase)
        val hasUppercase = password.any(Char::isUpperCase)
        val hasDigit = password.any(Char::isDigit)

        val categoryCount = listOf(hasLowercase, hasUppercase, hasDigit).count { it }

        return categoryCount >= 2
    }
}
