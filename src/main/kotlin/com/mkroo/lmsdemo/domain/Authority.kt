package com.mkroo.lmsdemo.domain

import org.springframework.security.core.GrantedAuthority

enum class Authority : GrantedAuthority {
    OPEN_LECTURE,
    LIST_LECTURES,
    APPLY_LECTURE;

    override fun getAuthority(): String {
        return name
    }
}