package com.mkroo.lmsdemo.application

import com.mkroo.lmsdemo.dao.LectureRepository
import com.mkroo.lmsdemo.domain.Account
import com.mkroo.lmsdemo.domain.Lecture
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LectureApplyingService(
    val lectureRepository: LectureRepository
) {
    @Transactional
    fun applyLecture(student: Account, lecture: Lecture) : Lecture {
        lecture.apply(student)

        return lectureRepository.save(lecture)
    }
}
