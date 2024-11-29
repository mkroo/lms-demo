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
    val authorities: Set<GrantedAuthority>,
) {
    companion object {
        private const val AUTHORITIES_CLAIM_KEY = "authorities"

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
            return claims.get(AUTHORITIES_CLAIM_KEY, List::class.java)
                .map { authorityString -> Authority.valueOf(authorityString.toString()) }
                .toSet()
        }
    }

    fun toClaims(expiresIn: Duration) : Claims {
        return Jwts.claims().apply {
            subject = accountId.toString()
            issuedAt = Date()
            expiration = Date(System.currentTimeMillis() + expiresIn.toMillis())
            set(AUTHORITIES_CLAIM_KEY, authorities)
        }
    }
}