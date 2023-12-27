package com.diegolirio.enemgamification.domain.usecase.exception

class EnrollmentDoesNotBelongException(message: String) : RuntimeException(message) {

    companion object {
        const val PROBLEM_DETAIL_TITLE = "Enrollment And Question are not compatible"
        const val PROBLEM_DETAIL_DETAIL = "Enrollment does not belong to this Test"
    }
}
