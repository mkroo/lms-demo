package com.mkroo.lmsdemo.dto

data class LectureOpeningRequest(
    val title: String,
    val maxStudentCount: Long,
    val price: Long
)
