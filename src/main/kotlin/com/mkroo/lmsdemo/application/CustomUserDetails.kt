package com.mkroo.lmsdemo.application

import com.mkroo.lmsdemo.domain.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class CustomUserDetails(
    private val user: User
) : UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mutableListOf(SimpleGrantedAuthority(user.userType.name))
    }

    override fun getPassword(): String {
        return user.encodedPassword
    }

    override fun getUsername(): String {
        return user.email
    }
}