package com.mkroo.lmsdemo.application

import com.mkroo.lmsdemo.dao.LectureRepository
import com.mkroo.lmsdemo.dao.TeacherRepository
import com.mkroo.lmsdemo.domain.Lecture
import com.mkroo.lmsdemo.dto.LectureOpeningRequest
import com.mkroo.lmsdemo.exception.IllegalAuthenticationException
import com.mkroo.lmsdemo.security.AccountJwtAuthentication
import org.springframework.stereotype.Service

@Service
class LectureOpeningService(
    private val lectureRepository: LectureRepository,
    private val teacherRepository: TeacherRepository,
) {
    fun openLecture(authentication: AccountJwtAuthentication, request: LectureOpeningRequest) : Lecture {
        val teacher = teacherRepository.findById(authentication.accountId) ?: throw IllegalAuthenticationException("Teacher must be present")

        val lecture = Lecture(
            request.title,
            request.maxStudentCount,
            request.price,
            teacher = teacher
        )

        return lectureRepository.save(lecture)
    }
}
