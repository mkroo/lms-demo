package com.mkroo.lmsdemo.application

import com.mkroo.lmsdemo.dao.LectureRepository
import com.mkroo.lmsdemo.domain.Account
import com.mkroo.lmsdemo.domain.Lecture
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration

@Service
class LectureApplyingService(
    val lectureRepository: LectureRepository,
    val lockClient: LockClient,
) {
    @Transactional
    fun applyLecture(student: Account, lecture: Lecture) : Lecture {
        val lockKey = "lecture:${lecture.id}"

        if (student.id == lecture.teacher.id) {
            throw IllegalArgumentException("자신의 강의를 신청할 수 없습니다.")
        }

        return lockClient.tryLock(lockKey, Duration.ofSeconds(1), Duration.ofSeconds(3)) {
            lecture.apply(student)
            lectureRepository.save(lecture)
        }
    }
}
