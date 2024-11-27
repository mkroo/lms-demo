package com.mkroo.lmsdemo.dto

data class LectureOpeningRequest(
    val title: String,
    val maxStudentCount: Int,
    val price: Long
)
