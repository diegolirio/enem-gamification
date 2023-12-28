package com.diegolirio.enemgamification.domain.usecase.exception

class AnswerOutOfBoundsException : RuntimeException() {

    companion object {
        const val PROBLEM_DETAIL_TITLE = "Answer out of bounds"
        const val PROBLEM_DETAIL_DETAIL = "Answer out of bounds, it should be between A and D"
    }

}
