package com.mkroo.lmsdemo.domain

import jakarta.persistence.*

@Entity
@Table(name = "lectures")
class Lecture(
    val title: String,
    val maxStudentCount: Int,
    val price: Long,
    @ManyToOne(optional = false)
    val teacher: Teacher
) : AbstractEntity() {
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "lecture", cascade = [CascadeType.PERSIST])
    private val _applications: MutableList<LectureApplication> = mutableListOf()

    @get:Transient
    val applicationCount: Int
        get() = _applications.size

    fun apply(student: Account) {
        if (_applications.size >= maxStudentCount) {
            throw IllegalStateException("This lecture is full")
        }

        _applications.add(LectureApplication(this, student))
    }
}
