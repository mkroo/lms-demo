package com.mkroo.lmsdemo.domain

import jakarta.persistence.*

@Entity
@Table(name = "lectures")
class Lecture(
    val title: String,
    val maxStudentCount: Int,
    val price: Int,
    @ManyToOne
    val teacher: Teacher
) : AbstractEntity()
