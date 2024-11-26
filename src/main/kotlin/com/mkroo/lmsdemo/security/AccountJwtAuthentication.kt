package com.mkroo.lmsdemo.security

import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority

class AccountJwtAuthentication(
    val accountId: Long,
    private val grantedAuthorities: Set<GrantedAuthority>
) : Authentication {
    override fun getName(): String {
        return "account:$accountId"
    }

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return grantedAuthorities
    }

    override fun getDetails() = mapOf("accountId" to accountId)

    override fun getPrincipal(): String {
        return accountId.toString()
    }

    override fun getCredentials(): Any {
        throw IllegalStateException("AccountJwtAuthentication does not have credentials")
    }

    override fun isAuthenticated(): Boolean {
        return true
    }

    override fun setAuthenticated(isAuthenticated: Boolean) {
        throw IllegalStateException("AccountJwtAuthentication is always authenticated")
    }

    override fun toString(): String {
        return "AccountJwtAuthentication(accountId=$accountId, grantedAuthorities=$grantedAuthorities)"
    }
}