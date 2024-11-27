package com.mkroo.lmsdemo.domain

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime

@Entity
@Table(name = "lectures")
class Lecture(
    val title: String,
    val maxStudentCount: Long,
    val price: Long,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    val teacher: Teacher
) : AbstractEntity() {
    @CreatedDate
    val createdAt: LocalDateTime = LocalDateTime.now()

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "lecture", cascade = [CascadeType.ALL])
    private val _applications: MutableList<LectureApplication> = mutableListOf()

    fun getApplications(): Set<LectureApplication> {
        return _applications.toSet()
    }

    fun apply(student: Student) {
        if (_applications.size >= maxStudentCount) {
            throw IllegalStateException("This lecture is full")
        }

        _applications.add(LectureApplication(this, student))
    }
}
