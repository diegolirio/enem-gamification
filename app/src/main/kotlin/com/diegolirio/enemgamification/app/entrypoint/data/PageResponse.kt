package com.diegolirio.enemgamification.app.entrypoint.data

import com.diegolirio.enemgamification.domain.entity.QuestionEntity
import org.springframework.data.domain.Page

data class PageResponse<T>(
        var content: List<T>,
        var pageNumber: Int,
        var pageSize: Int,
        var totalPages: Int,
        var totalElements: Long,
        var last: Boolean = false,
        var first:  Boolean = false,
        var empty: Boolean = false,
)

fun Page<QuestionEntity>.toResponse(): PageResponse<QuestionResponse> =
        PageResponse(
                content = this.content.map(QuestionEntity::toResponse),
                pageNumber = this.number,
                pageSize = this.size,
                totalPages = this.totalPages,
                totalElements = this.totalElements,
                last = this.isLast,
                first = this.isFirst,
                empty = this.isEmpty
        )

