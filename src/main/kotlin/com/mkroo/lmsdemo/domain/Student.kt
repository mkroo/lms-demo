package com.mkroo.lmsdemo.domain

import jakarta.persistence.*

@Entity
@Table(name = "students")
@DiscriminatorValue("student")
class Student(
    email: String,
    name: String,
    phoneNumber: String,
    encodedPassword: String,
) : Account(email, encodedPassword, name, phoneNumber) {
    @get:Transient
    override val authorities: Set<Authority> = setOf(
        Authority.LIST_LECTURES,
        Authority.APPLY_LECTURE,
    )
}
