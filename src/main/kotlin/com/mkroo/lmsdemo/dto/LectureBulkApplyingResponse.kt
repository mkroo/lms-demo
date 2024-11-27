package com.mkroo.lmsdemo.dto

data class LectureBulkApplyingResponse(
    val appliedLectureIds: List<Long>,
    val failedLectures: List<FailedLecture>,
) {
    data class FailedLecture(
        val id: Long,
        val reason: String,
    )
}
