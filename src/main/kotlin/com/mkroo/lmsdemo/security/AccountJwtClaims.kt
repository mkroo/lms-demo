package com.mkroo.lmsdemo.security

import com.mkroo.lmsdemo.domain.Account
import com.mkroo.lmsdemo.domain.Authority
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import org.springframework.security.core.GrantedAuthority
import java.time.Duration
import java.util.*

data class AccountJwtClaims(
    val accountId: Long,
    val role: Set<GrantedAuthority>,
) {
    companion object {
        private const val ROLES_DELIMITER = ","
        private const val ROLE_CLAIM_KEY = "role"

        fun of(account: Account) : AccountJwtClaims {
            return AccountJwtClaims(
                account.id,
                account.authorities
            )
        }

        fun of(claims: Claims) : AccountJwtClaims {
            return AccountJwtClaims(
                claims.subject.toLong(),
                obtainAuthorities(claims)
            )
        }

        private fun obtainAuthorities(claims: Claims) : Set<GrantedAuthority> {
            return claims.get(ROLE_CLAIM_KEY, String::class.java)
                .split(ROLES_DELIMITER)
                .map(Authority::valueOf)
                .toSet()
        }
    }

    fun toClaims(expiresIn: Duration) : Claims {
        return Jwts.claims().apply {
            subject = accountId.toString()
            issuedAt = Date()
            expiration = Date(System.currentTimeMillis() + expiresIn.toMillis())
            set(ROLE_CLAIM_KEY, role.joinToString(ROLES_DELIMITER))
        }
    }
}