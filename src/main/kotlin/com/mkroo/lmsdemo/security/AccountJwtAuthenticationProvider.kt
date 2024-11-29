package com.mkroo.lmsdemo.security

class AccountJwtAuthenticationProvider(
    private val jwtUtils: JwtUtils
) {
    fun authenticate(token: String) : AccountJwtAuthentication {
        val claims = jwtUtils.parse(token)
        return AccountJwtAuthentication(claims.accountId, claims.authorities)
    }
}