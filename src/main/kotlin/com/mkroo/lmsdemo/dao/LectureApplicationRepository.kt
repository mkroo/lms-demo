package com.mkroo.lmsdemo.dao

import com.mkroo.lmsdemo.domain.Account
import com.mkroo.lmsdemo.domain.Lecture
import com.mkroo.lmsdemo.domain.LectureApplication
import org.springframework.data.repository.Repository

interface LectureApplicationRepository : Repository<LectureApplication, Long> {
    fun existsByLectureAndStudent(lecture: Lecture, student: Account): Boolean
}