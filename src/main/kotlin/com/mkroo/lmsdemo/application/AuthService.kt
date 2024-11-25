package com.mkroo.lmsdemo.application

import com.mkroo.lmsdemo.dao.UserRepository
import com.mkroo.lmsdemo.domain.PasswordValidator
import com.mkroo.lmsdemo.domain.User
import com.mkroo.lmsdemo.dto.RegisterUserRequest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val userRepository: UserRepository,
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

        val user = User(
            name = request.name,
            email = request.email,
            phoneNumber = request.phoneNumber,
            encodedPassword = encodedPassword,
            userType = request.userType
        )

        userRepository.save(user)
    }

    private fun checkDuplicateEmail(request: RegisterUserRequest) {
        userRepository.existsByEmail(request.email).takeIf { it }?.let {
            throw IllegalArgumentException("Email already exists")
        }
    }

    private fun checkDuplicatePhoneNumber(request: RegisterUserRequest) {
        userRepository.existsByPhoneNumber(request.phoneNumber).takeIf { it }?.let {
            throw IllegalArgumentException("Phone number already exists")
        }
    }

    private fun checkPasswordConstraints(request: RegisterUserRequest) {
        passwordValidator.isValid(request.password).takeIf { !it }?.let {
            throw IllegalArgumentException("Invalid password")
        }
    }
}