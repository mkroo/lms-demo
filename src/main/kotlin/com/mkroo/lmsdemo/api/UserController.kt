package com.mkroo.lmsdemo.api

import com.mkroo.lmsdemo.application.AuthService
import com.mkroo.lmsdemo.dto.RegisterUserRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(
    private val authService: AuthService
) {
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun register(@RequestBody request: RegisterUserRequest) {
        authService.register(request)
    }
}
