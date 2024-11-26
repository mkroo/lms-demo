package com.mkroo.lmsdemo.api

import com.mkroo.lmsdemo.application.LectureOpeningService
import com.mkroo.lmsdemo.dto.LectureOpeningRequest
import com.mkroo.lmsdemo.security.AccountJwtAuthentication
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class LectureController(
    private val lectureOpeningService: LectureOpeningService
) {
    @PostMapping("/lectures")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun openLecture(
        authentication: AccountJwtAuthentication,
        @RequestBody request: LectureOpeningRequest,
    ) {
        lectureOpeningService.openLecture(authentication, request)
    }
}
