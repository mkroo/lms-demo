package com.mkroo.lmsdemo.dao

import com.mkroo.lmsdemo.domain.Account
import org.springframework.data.repository.Repository

interface AccountRepository : Repository<Account, Long> {
    fun findById(id: Long): Account?
    fun findByEmail(email: String): Account?
    fun existsByEmail(email: String): Boolean
    fun existsByPhoneNumber(phoneNumber: String): Boolean
    fun save(account: Account): Account
}
