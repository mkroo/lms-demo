package com.mkroo.lmsdemo.application

import com.mkroo.lmsdemo.dao.LectureApplicationRepository
import com.mkroo.lmsdemo.dao.LectureRepository
import com.mkroo.lmsdemo.domain.Account
import com.mkroo.lmsdemo.domain.Lecture
import com.mkroo.lmsdemo.exception.LectureApplyingException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration

@Service
class LectureApplyingService(
    val lectureRepository: LectureRepository,
    val lectureApplicationRepository: LectureApplicationRepository,
    val lockClient: LockClient,
) {
    @Transactional
    fun applyLecture(student: Account, lecture: Lecture) : Lecture {
        val lockKey = "lecture:${lecture.id}"

        return lockClient.tryLock(lockKey, Duration.ofSeconds(1), Duration.ofSeconds(3)) {
            if (lectureApplicationRepository.existsByLectureAndStudent(lecture, student)) {
                throw LectureApplyingException("이미 신청한 강의입니다.")
            }

            lecture.apply(student)
            lectureRepository.save(lecture)
        }
    }
}
