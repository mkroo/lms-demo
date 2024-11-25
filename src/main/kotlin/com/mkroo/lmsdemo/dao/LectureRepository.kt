package com.mkroo.lmsdemo.dao

import com.mkroo.lmsdemo.domain.Lecture
import org.springframework.data.repository.Repository

interface LectureRepository : Repository<Lecture, Long> {
    fun save(lecture: Lecture): Lecture
}