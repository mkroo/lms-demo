package com.mkroo.lmsdemo.dto

import java.time.LocalDateTime

data class LectureApplyStatus(
    val id: Long,
    val title: String,
    val price: Long,
    val teacherName: String,
    val currentStudentCount: Int,
    val maxStudentCount: Int,
    val createdAt: LocalDateTime,
)