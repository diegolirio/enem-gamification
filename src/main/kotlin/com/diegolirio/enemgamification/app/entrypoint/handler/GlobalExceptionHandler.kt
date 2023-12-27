package com.diegolirio.enemgamification.app.entrypoint.handler

import com.diegolirio.enemgamification.domain.usecase.exception.AlreadyAnsweredException
import com.diegolirio.enemgamification.domain.usecase.exception.AnswerOutOfBoundsException
import com.diegolirio.enemgamification.domain.usecase.exception.EnrollmentDoesNotBelongException
import com.diegolirio.enemgamification.domain.usecase.exception.PageNumberAndPageSizeFormatException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.time.Instant


@RestControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(PageNumberAndPageSizeFormatException::class)
    fun handlePageNumberAndPageSizeFormatException(e: PageNumberAndPageSizeFormatException): ProblemDetail {
        val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.localizedMessage)
        problemDetail.title = PageNumberAndPageSizeFormatException.PROBLEM_DETAIL_TITLE
        problemDetail.detail = PageNumberAndPageSizeFormatException.PROBLEM_DETAIL_DETAIL
        //problemDetail.setProperty("StackTrace", e.stackTrace)
        problemDetail.setProperty("TimeStamp", Instant.now())
        return problemDetail
    }

    @ExceptionHandler(AlreadyAnsweredException::class)
    fun handleAlreadyAnsweredException(e: AlreadyAnsweredException) : ProblemDetail {
        val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, e.localizedMessage)
        problemDetail.title = AlreadyAnsweredException.PROBLEM_DETAIL_TITLE
        problemDetail.detail = AlreadyAnsweredException.PROBLEM_DETAIL_DETAIL
        problemDetail.setProperty("Timestamp", Instant.now())
        return problemDetail
    }

    @ExceptionHandler(AnswerOutOfBoundsException::class)
    fun handleAnswerOutOfBoundsException(e: AnswerOutOfBoundsException) : ProblemDetail {
        val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, e.localizedMessage)
        problemDetail.title = AnswerOutOfBoundsException.PROBLEM_DETAIL_TITLE
        problemDetail.detail = AnswerOutOfBoundsException.PROBLEM_DETAIL_DETAIL
        problemDetail.setProperty("Timestamp", Instant.now())
        return problemDetail
    }

    @ExceptionHandler(EnrollmentDoesNotBelongException::class)
    fun handleEnrollmentDoesNotBelongException(e: EnrollmentDoesNotBelongException) : ProblemDetail {
        val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, e.localizedMessage)
        problemDetail.title = EnrollmentDoesNotBelongException.PROBLEM_DETAIL_TITLE
        problemDetail.detail = EnrollmentDoesNotBelongException.PROBLEM_DETAIL_DETAIL
        problemDetail.setProperty("Timestamp", Instant.now())
        return problemDetail
    }

}