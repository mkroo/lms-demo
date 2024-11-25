package com.mkroo.lmsdemo.infrastructure

import com.mkroo.lmsdemo.application.AuthenticationTokenProvider
import com.mkroo.lmsdemo.dao.UserRepository
import com.mkroo.lmsdemo.domain.User
import com.mkroo.lmsdemo.exception.AuthenticationTokenParseException
import org.springframework.stereotype.Service

// TODO: JWT 토큰 기반으로 변경
@Service
class SimpleAuthenticationTokenProvider(
    private val userRepository: UserRepository
) : AuthenticationTokenProvider {
    override fun issue(user: User): String {
        return user.email
    }

    override fun parse(token: String): User {
        return userRepository.findByEmail(token) ?: throw AuthenticationTokenParseException("User not found")
    }
}
