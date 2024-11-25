package com.mkroo.lmsdemo.api

import com.mkroo.lmsdemo.exception.LoginFailureException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleIllegalArgumentException(e: IllegalArgumentException): String {
        return e.message!!
    }

    @ExceptionHandler(LoginFailureException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleLoginFailureException(e: LoginFailureException): String {
        return e.message!!
    }
}