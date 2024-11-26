package com.mkroo.lmsdemo.application

import com.mkroo.lmsdemo.dao.AccountRepository
import com.mkroo.lmsdemo.domain.PasswordValidator
import com.mkroo.lmsdemo.domain.Student
import com.mkroo.lmsdemo.domain.Teacher
import com.mkroo.lmsdemo.dto.RegisterUserRequest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val accountRepository: AccountRepository,
    private val passwordEncoder: PasswordEncoder,
    private val passwordValidator: PasswordValidator
) {
    @Transactional
    fun register(request: RegisterUserRequest) {
        request
            .apply(::checkDuplicateEmail)
            .apply(::checkDuplicatePhoneNumber)
            .apply(::checkPasswordConstraints)

        val encodedPassword = passwordEncoder.encode(request.password)

        val account = when (request.role) {
            "student" -> Student(
                email = request.email,
                encodedPassword = encodedPassword,
                name = request.name,
                phoneNumber = request.phoneNumber
            )
            "teacher" -> Teacher(
                email = request.email,
                encodedPassword = encodedPassword,
                name = request.name,
                phoneNumber = request.phoneNumber
            )
            else -> throw IllegalArgumentException("Invalid role")
        }

        accountRepository.save(account)
    }

    private fun checkDuplicateEmail(request: RegisterUserRequest) {
        accountRepository.existsByEmail(request.email).takeIf { it }?.let {
            throw IllegalArgumentException("Email already exists")
        }
    }

    private fun checkDuplicatePhoneNumber(request: RegisterUserRequest) {
        accountRepository.existsByPhoneNumber(request.phoneNumber).takeIf { it }?.let {
            throw IllegalArgumentException("Phone number already exists")
        }
    }

    private fun checkPasswordConstraints(request: RegisterUserRequest) {
        passwordValidator.isValid(request.password).takeIf { !it }?.let {
            throw IllegalArgumentException("Invalid password")
        }
    }
}