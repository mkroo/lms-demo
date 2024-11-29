package com.mkroo.lmsdemo.dto

import org.springframework.data.domain.Page

internal typealias RestApiErrorResponse = RestApiResponse<Map<String, String>>
internal typealias RestApiEmptyResponse = RestApiResponse<Unit>
internal typealias RestApiPageResponse<T> = RestApiResponse<RestApiResponse.PageResponse<T>>

data class RestApiResponse<T>(
    val status: String,
    val data: T,
) {
    companion object {
        fun <T> success(data: T): RestApiResponse<T> {
            return RestApiResponse("success", data)
        }

        fun <T> success(page: Page<T>): RestApiPageResponse<T> {
            return RestApiResponse("success", PageResponse(page))
        }

        fun empty(): RestApiEmptyResponse {
            return RestApiResponse("success", Unit)
        }

        fun error(exception: Exception): RestApiErrorResponse {
            return RestApiResponse("error", mapOf("message" to exception.localizedMessage))
        }
    }

    data class PageResponse<T>(
        val items: List<T>,
        val page: Int,
        val size: Int,
        val totalPages: Int,
        val totalItems: Long,
    ) {
        constructor(page: Page<T>) : this(
            items = page.content,
            page = page.number,
            size = page.size,
            totalPages = page.totalPages,
            totalItems = page.totalElements,
        )
    }
}
