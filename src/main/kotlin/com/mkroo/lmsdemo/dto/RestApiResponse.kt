package com.mkroo.lmsdemo.dto

internal typealias RestApiErrorResponse = RestApiResponse<Map<String, String>>

data class RestApiResponse<T>(
    val status: String,
    val data: T,
) {
    companion object {
        fun <T> success(data: T): RestApiResponse<T> {
            return RestApiResponse("success", data)
        }

        fun error(exception: Exception): RestApiErrorResponse {
            return RestApiResponse("error", mapOf("message" to exception.localizedMessage))
        }
    }
}
