package com.mkroo.lmsdemo.domain

import jakarta.persistence.*

@Entity
@Table(name = "users")
class User(
    val name: String,
    val email: String,
    val phoneNumber: String,
    val encodedPassword: String,
    @Enumerated(EnumType.STRING)
    val role: UserRole
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
}
