package com.mkroo.lmsdemo.application

import com.mkroo.lmsdemo.domain.User

interface AuthenticationTokenProvider {
    fun issue(user: User) : String
    fun parse(token: String) : User
}
