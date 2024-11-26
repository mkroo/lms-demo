package com.mkroo.lmsdemo.application

import com.mkroo.lmsdemo.dao.AccountRepository
import com.mkroo.lmsdemo.dto.LoginRequest
import com.mkroo.lmsdemo.dto.LoginResponse
import com.mkroo.lmsdemo.exception.LoginFailureException
import com.mkroo.lmsdemo.security.JwtUtils
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class LoginService(
    private val accountRepository: AccountRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtUtils: JwtUtils
) {
    fun login(request: LoginRequest) : LoginResponse {
        val account = accountRepository.findByEmail(request.email) ?: throw LoginFailureException("Account not found")

        passwordEncoder.matches(request.password, account.encodedPassword).takeIf { !it }?.let {
            throw LoginFailureException("Wrong password")
        }

        val token = jwtUtils.issue(account)

        return LoginResponse(token)
    }
}
