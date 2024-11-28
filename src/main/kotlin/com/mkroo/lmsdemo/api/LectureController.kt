package com.mkroo.lmsdemo.api

import com.mkroo.lmsdemo.application.LectureBulkApplyingService
import com.mkroo.lmsdemo.application.LectureListingService
import com.mkroo.lmsdemo.application.LectureOpeningService
import com.mkroo.lmsdemo.dto.LectureApplyStatus
import com.mkroo.lmsdemo.dto.LectureBulkApplyingRequest
import com.mkroo.lmsdemo.dto.LectureBulkApplyingResponse
import com.mkroo.lmsdemo.dto.LectureOpeningRequest
import com.mkroo.lmsdemo.security.AccountJwtAuthentication
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
class LectureController(
    private val lectureOpeningService: LectureOpeningService,
    private val lectureListingService: LectureListingService,
    private val lectureBulkApplyingService: LectureBulkApplyingService,
) {
    @PostMapping("/lectures")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun openLecture(
        authentication: AccountJwtAuthentication,
        @RequestBody request: LectureOpeningRequest,
    ) {
        lectureOpeningService.openLecture(authentication, request)
    }

    @GetMapping("/lectures")
    fun listLectures(
        @PageableDefault(page = 0, size = 20)
        pageable: Pageable
    ) : Page<LectureApplyStatus> {
        return lectureListingService.listLectures(pageable)
    }

    @PostMapping("/lecture-applications")
    fun applyLectures(
        @RequestBody request: LectureBulkApplyingRequest,
        authentication: AccountJwtAuthentication,
    ) : LectureBulkApplyingResponse {
        return lectureBulkApplyingService.applyLectures(authentication, request)
    }
}
