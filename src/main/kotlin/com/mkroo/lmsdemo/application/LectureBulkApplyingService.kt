package com.mkroo.lmsdemo.application

import com.mkroo.lmsdemo.dao.AccountRepository
import com.mkroo.lmsdemo.dao.LectureRepository
import com.mkroo.lmsdemo.dto.LectureBulkApplyingRequest
import com.mkroo.lmsdemo.dto.LectureBulkApplyingResponse
import com.mkroo.lmsdemo.security.AccountJwtAuthentication
import org.springframework.security.access.annotation.Secured
import org.springframework.stereotype.Service

@Service
class LectureBulkApplyingService(
    private val lectureApplyingService: LectureApplyingService,
    private val lectureRepository: LectureRepository,
    private val accountRepository: AccountRepository,
) {
    @Secured("APPLY_LECTURE")
    fun applyLectures(authentication: AccountJwtAuthentication, request: LectureBulkApplyingRequest) : LectureBulkApplyingResponse {
        val student = accountRepository.findById(authentication.accountId) ?: throw IllegalArgumentException("Account not found")

        val lectures = lectureRepository.findAllByIdIn(request.lectureIds)
        val lectureIdMap = lectures.associateBy { it.id }

        val appliedLectureIds: MutableList<Long> = mutableListOf()
        val failedLectures: MutableList<LectureBulkApplyingResponse.FailedLecture> = mutableListOf()

        for (lectureId in request.lectureIds) {
            val lecture = lectureIdMap[lectureId]
            if (lecture == null) {
                failedLectures.add(LectureBulkApplyingResponse.FailedLecture(lectureId, "Lecture not found"))
                continue
            }

            try {
                lectureApplyingService.applyLecture(student, lecture)
                appliedLectureIds.add(lectureId)
            } catch (e: Exception) {
                failedLectures.add(LectureBulkApplyingResponse.FailedLecture(lectureId, e.localizedMessage))
            }
        }

        return LectureBulkApplyingResponse(appliedLectureIds, failedLectures)
    }
}
