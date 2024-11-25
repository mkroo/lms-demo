package com.mkroo.lmsdemo.dao

import com.mkroo.lmsdemo.domain.User
import org.springframework.data.repository.Repository

interface UserRepository : Repository<User, Long> {
    fun findByEmail(email: String): User?
    fun existsByEmail(email: String): Boolean
    fun existsByPhoneNumber(phoneNumber: String): Boolean
    fun save(user: User): User
}
