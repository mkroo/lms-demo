package com.mkroo.lmsdemo.dto

import com.mkroo.lmsdemo.domain.UserRole
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class RegisterUserRequest(
    @field:NotBlank(message = "Name is required")
    val name: String,

    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Invalid email format")
    val email: String,

    @field:NotBlank(message = "Phone number is required")
    val phoneNumber: String,

    @field:NotBlank(message = "Password is required")
    val password: String,

    @field:NotBlank(message = "User role is required")
    val role: UserRole
)
