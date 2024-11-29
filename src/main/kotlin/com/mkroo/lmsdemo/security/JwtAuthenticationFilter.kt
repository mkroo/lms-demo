package com.mkroo.lmsdemo.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.mkroo.lmsdemo.dto.RestApiResponse
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.util.matcher.RequestMatcher
import org.springframework.web.filter.OncePerRequestFilter

class JwtAuthenticationFilter(
    private val authenticateRequestMatcher: RequestMatcher,
    private val authenticationProvider: AccountJwtAuthenticationProvider,
    private val objectMapper: ObjectMapper,
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
            } catch (e: AccountAuthenticationException) {
                unsuccessfulAuthentication(request, response, e)
            } catch (e: Exception) {
                logger.warn("Unexpected error occurred (${request.method} ${request.requestURI}): ${e.message} (${e.javaClass.name})")

                unsuccessfulAuthentication(request, response, AccountAuthenticationException("Unexpected error occurred", e))
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

        val authorizationParts = authorizationValue.split(" ")
        if (authorizationParts.size != 2) throw AccountAuthenticationException("Authorization header is malformed")

        val (scheme, token) = authorizationParts

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
        failed: AccountAuthenticationException,
    ) {
        logger.error("Authentication failed (${request.method} ${request.requestURI}): ${failed.message}")

        response.status = HttpStatus.UNAUTHORIZED.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        objectMapper.writeValue(response.writer, RestApiResponse.error(failed))
    }

    class AccountAuthenticationException(msg: String, t: Throwable? = null) : AuthenticationException(msg, t)
}
