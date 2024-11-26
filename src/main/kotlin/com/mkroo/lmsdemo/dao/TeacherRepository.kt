package com.mkroo.lmsdemo.dao

import com.mkroo.lmsdemo.domain.Teacher
import org.springframework.data.repository.Repository

interface TeacherRepository : Repository<Teacher, Long> {
    fun findById(id: Long): Teacher?
}