package com.mkroo.lmsdemo.application

import com.mkroo.lmsdemo.dao.UserRepository
import com.mkroo.lmsdemo.dto.LoginRequest
import com.mkroo.lmsdemo.dto.LoginResponse
import com.mkroo.lmsdemo.exception.LoginFailureException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class LoginService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val authenticationTokenProvider: AuthenticationTokenProvider
) {
    fun login(request: LoginRequest) : LoginResponse {
        val user = userRepository.findByEmail(request.email) ?: throw LoginFailureException("User not found")

        passwordEncoder.matches(request.password, user.encodedPassword).takeIf { !it }?.let {
            throw LoginFailureException("Wrong password")
        }

        val token = authenticationTokenProvider.issue(user)

        return LoginResponse(token)
    }
}
