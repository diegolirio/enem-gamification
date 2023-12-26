package com.diegolirio.enemgamification.domain.usecase.exception

class AlreadyAnsweredException(message: String) : RuntimeException(message) {

    companion object {
        const val PROBLEM_DETAIL_TITLE = "Duplicate answer"
        const val PROBLEM_DETAIL_DETAIL = "This question has already been answered, you cannot send again!"
    }

}
