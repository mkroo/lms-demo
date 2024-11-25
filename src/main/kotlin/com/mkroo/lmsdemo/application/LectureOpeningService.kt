package com.mkroo.lmsdemo.application

import com.mkroo.lmsdemo.dao.LectureRepository
import com.mkroo.lmsdemo.domain.Lecture
import com.mkroo.lmsdemo.domain.User
import com.mkroo.lmsdemo.domain.UserRole
import com.mkroo.lmsdemo.dto.LectureOpeningRequest
import com.mkroo.lmsdemo.exception.NotPermittedException
import org.springframework.stereotype.Service

@Service
class LectureOpeningService(
    private val lectureRepository: LectureRepository
) {
    fun openLecture(teacher: User, request: LectureOpeningRequest) : Lecture {
        checkPermission(teacher)

        val lecture = Lecture(
            request.title,
            request.maxStudentCount,
            request.price,
            teacher
        )

        return lectureRepository.save(lecture)
    }

    private fun checkPermission(user: User) {
        if (user.role != UserRole.TEACHER) throw NotPermittedException("강의를 개설할 권한이 없습니다.")
    }
}
