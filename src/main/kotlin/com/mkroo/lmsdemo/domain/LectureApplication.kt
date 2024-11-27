package com.mkroo.lmsdemo.domain

import jakarta.persistence.Entity
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "lecture_applications")
class LectureApplication(
    @ManyToOne
    val lecture: Lecture,
    @ManyToOne
    val student: Account,
) : AbstractEntity()
