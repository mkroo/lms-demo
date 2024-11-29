package com.mkroo.lmsdemo.api

import com.mkroo.lmsdemo.application.AuthService
import com.mkroo.lmsdemo.application.LoginService
import com.mkroo.lmsdemo.dto.*
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(
    private val authService: AuthService,
    private val loginService: LoginService
) {
    @PostMapping("/register")
    fun register(@RequestBody request: RegisterUserRequest) : RestApiEmptyResponse {
        authService.register(request)

        return RestApiResponse.empty()
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): RestApiResponse<LoginResponse> {
        return loginService.login(request).let { RestApiResponse.success(it) }
    }
}
