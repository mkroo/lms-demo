package com.mkroo.lmsdemo.domain

import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.Transient

@Entity
@Table(name = "teachers")
@DiscriminatorValue("teacher")
class Teacher(
    email: String,
    name: String,
    phoneNumber: String,
    encodedPassword: String,
) : Account(email, encodedPassword, name, phoneNumber) {
    @get:Transient
    override val authorities: Set<Authority> = setOf(
        Authority.OPEN_LECTURE,
        Authority.LIST_LECTURES,
        Authority.APPLY_LECTURE
    )
}

