package com.mkroo.lmsdemo.api

import com.mkroo.lmsdemo.dto.RestApiErrorResponse
import com.mkroo.lmsdemo.dto.RestApiResponse
import com.mkroo.lmsdemo.exception.*
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(
        IllegalArgumentException::class,
        LoginFailureException::class,
        UserRegistrationFieldException::class,
        LectureListingParamsException::class,
        LectureApplyingException::class
    )
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleIllegalArgumentException(e: Exception): RestApiErrorResponse {
        return RestApiResponse.error(e)
    }

    @ExceptionHandler(IllegalAuthenticationException::class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun handleIllegalAuthenticationException(e: IllegalAuthenticationException): RestApiErrorResponse {
        return RestApiResponse.error(e)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleHttpMessageNotReadableException(e: HttpMessageNotReadableException): RestApiErrorResponse {
        return RestApiResponse.error(Exception("올바르지 않은 요청입니다."))
    }
}
