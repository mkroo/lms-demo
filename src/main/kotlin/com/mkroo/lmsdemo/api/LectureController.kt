package com.mkroo.lmsdemo.api

import com.mkroo.lmsdemo.application.LectureBulkApplyingService
import com.mkroo.lmsdemo.application.LectureListingService
import com.mkroo.lmsdemo.application.LectureOpeningService
import com.mkroo.lmsdemo.dto.*
import com.mkroo.lmsdemo.security.AccountJwtAuthentication
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class LectureController(
    private val lectureOpeningService: LectureOpeningService,
    private val lectureListingService: LectureListingService,
    private val lectureBulkApplyingService: LectureBulkApplyingService,
) {
    @PostMapping("/lectures")
    fun openLecture(
        authentication: AccountJwtAuthentication,
        @RequestBody request: LectureOpeningRequest,
    ) : RestApiEmptyResponse {
        lectureOpeningService.openLecture(authentication, request)

        return RestApiResponse.empty()
    }

    @GetMapping("/lectures")
    fun listLectures(
        @PageableDefault(page = 0, size = 20)
        pageable: Pageable
    ) : RestApiPageResponse<LectureApplyStatus> {
        return lectureListingService.listLectures(pageable).let { RestApiResponse.success(it) }
    }

    @PostMapping("/lecture-applications")
    fun applyLectures(
        @RequestBody request: LectureBulkApplyingRequest,
        authentication: AccountJwtAuthentication,
    ) : RestApiResponse<LectureBulkApplyingResponse> {
        return lectureBulkApplyingService.applyLectures(authentication, request).let { RestApiResponse.success(it) }
    }
}
