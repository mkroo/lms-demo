package com.mkroo.lmsdemo.domain

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime

@Entity
@Table(name = "lectures")
class Lecture(
    val title: String,
    val maxStudentCount: Int,
    val price: Long,
    @ManyToOne(optional = false)
    val teacher: Teacher
) : AbstractEntity() {
    @CreatedDate
    val createdAt: LocalDateTime = LocalDateTime.now()

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
