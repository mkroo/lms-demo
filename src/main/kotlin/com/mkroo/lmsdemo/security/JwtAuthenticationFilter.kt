package com.mkroo.lmsdemo.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.util.matcher.RequestMatcher
import org.springframework.web.filter.OncePerRequestFilter

class JwtAuthenticationFilter(
    private val authenticateRequestMatcher: RequestMatcher,
    private val authenticationProvider: AccountJwtAuthenticationProvider,
) : OncePerRequestFilter() {
    companion object {
        private const val AUTHORIZATION_HEADER = "Authorization"
        private const val AUTHORIZATION_SCHEME = "Bearer"
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        if (requiresAuthentication(request)) {
            try {
                val token = obtainAuthorizationToken(request)
                val authentication = authenticationProvider.authenticate(token)

                successfulAuthentication(request, response, filterChain, authentication)
            } catch (e: AuthenticationException) {
                unsuccessfulAuthentication(request, response, e)
            }
        } else {
            filterChain.doFilter(request, response)
        }
    }

    private fun requiresAuthentication(request: HttpServletRequest): Boolean {
        return authenticateRequestMatcher.matcher(request).isMatch
    }

    private fun obtainAuthorizationToken(request: HttpServletRequest): String {
        val authorizationValue = request.getHeader(AUTHORIZATION_HEADER)
        if (authorizationValue.isNullOrBlank()) throw AccountAuthenticationException("Authorization header is missing")

        val (scheme, token) = authorizationValue.split(" ")

        if (scheme != AUTHORIZATION_SCHEME) throw AccountAuthenticationException("Authorization scheme is not supported")

        return token.takeIf(String::isNotBlank) ?: throw AccountAuthenticationException("Token is missing")
    }

    private fun successfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain,
        authResult: Authentication,
    ) {
        SecurityContextHolder.getContext().authentication = authResult
        chain.doFilter(request, response)
    }

    private fun unsuccessfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        failed: AuthenticationException,
    ) {
        logger.error("Authentication failed: ${failed.message}")
        response.sendError(HttpStatus.UNAUTHORIZED.value(), failed.message)
    }

    class AccountAuthenticationException(msg: String, t: Throwable? = null) : AuthenticationException(msg, t)
}
