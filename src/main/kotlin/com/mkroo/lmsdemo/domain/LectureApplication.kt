package com.mkroo.lmsdemo.domain

import jakarta.persistence.Entity
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "lecture_applications")
class LectureApplication(
    @ManyToOne(optional = false)
    val lecture: Lecture,
    @ManyToOne(optional = false)
    val student: Account,
) : AbstractEntity()
