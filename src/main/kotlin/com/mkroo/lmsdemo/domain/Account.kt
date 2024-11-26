package com.mkroo.lmsdemo.domain

import jakarta.persistence.*

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "role")
@Table(name = "accounts")
abstract class Account(
    val email: String,
    val encodedPassword: String,
    val name: String,
    val phoneNumber: String,
) : AbstractEntity() {
    @get:Transient
    abstract val authorities: Set<Authority>
}
