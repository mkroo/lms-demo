package com.mkroo.lmsdemo.domain

import jakarta.persistence.Entity
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "lectures")
class Lecture(
    val title: String,
    val maxStudentCount: Int,
    val price: Int,
    @ManyToOne
    val teacher: User,
) : AbstractEntity()
