package com.mkroo.lmsdemo.domain

import com.mkroo.lmsdemo.exception.LectureApplyingException
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
            throw LectureApplyingException("수강 인원이 마감되었습니다.")
        }

        _applications.add(LectureApplication(this, student))
    }
}
