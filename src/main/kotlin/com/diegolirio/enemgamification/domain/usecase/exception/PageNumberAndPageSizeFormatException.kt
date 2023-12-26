package com.diegolirio.enemgamification.domain.usecase.exception

class PageNumberAndPageSizeFormatException(message: String) : RuntimeException(message) {
    companion object {
        const val PROBLEM_DETAIL_TITLE = "PageNumber and PageSize must be numeric"
        const val PROBLEM_DETAIL_DETAIL = "You should use this endpoint something like that /v1/questions?pageNumber=0&pageSize=10"
    }
}
