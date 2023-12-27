package com.diegolirio.enemgamification.domain.usecase

import com.diegolirio.enemgamification.domain.dataproviders.repository.AnswerRepository
import com.diegolirio.enemgamification.domain.dataproviders.repository.EnrollmentRepository
import com.diegolirio.enemgamification.domain.dataproviders.repository.QuestionRepository
import com.diegolirio.enemgamification.domain.entity.AnswerEntity
import com.diegolirio.enemgamification.domain.entity.EnrollmentEntity
import com.diegolirio.enemgamification.domain.usecase.exception.AlreadyAnsweredException
import com.diegolirio.enemgamification.domain.usecase.exception.AnswerOutOfBoundsException
import com.diegolirio.enemgamification.domain.usecase.exception.EnrollmentDoesNotBelongException
import com.diegolirio.enemgamification.domain.usecase.input.AnswerRequest
import com.diegolirio.enemgamification.domain.usecase.output.AnswerResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SaveAnswerUsecase(
        private val questionRepository: QuestionRepository,
        private val answerRepository: AnswerRepository,
        private val enrollmentRepository: EnrollmentRepository
) {

    @Transactional
    fun save(enrollmentId: String, answerRequest: AnswerRequest) : AnswerResponse {
        checkConstraints(enrollmentId, answerRequest)
        val enrollment = enrollmentRepository.findById(enrollmentId).get()
        val questionEntity = questionRepository.findById(answerRequest.questionId).get()

        if(questionEntity.test!!.id != enrollment.test!!.id) {
            throw EnrollmentDoesNotBelongException("Enrollment and Question do not belong to the same Test")
        }

        val scoring = if (questionEntity.correctAnswer == answerRequest.answer) 10 else -5

        return AnswerEntity(
                question = questionEntity,
                answer = answerRequest.answer,
                scoring = scoring,
                enrollment = enrollment,
        ).let {
            AnswerResponse(scoring = answerRepository.save(it).scoring)
        }.also {
            enrollment.scoringLevel.scoringTotal += it.scoring
            enrollment.scoringLevel.rating = ratingEnum(enrollment.scoringLevel.scoringTotal)
            enrollmentRepository.save(enrollment)
        }

    }

    private fun ratingEnum(scoringTotal: Int) = when {
        scoringTotal < 50 -> EnrollmentEntity.RatingEnum.NEWBIE
        scoringTotal in 51..100 -> EnrollmentEntity.RatingEnum.KNOWLEDGEABLE
        scoringTotal in 101..200 -> EnrollmentEntity.RatingEnum.EXPERT
        else -> EnrollmentEntity.RatingEnum.MASTER // enrollment.scoringLevel.scoringTotal > 200
    }

    private fun checkConstraints(enrollmentId: String, answerRequest: AnswerRequest) {
        if (answerRequest.answer !in 'A'..'D') {
            throw AnswerOutOfBoundsException()
        }
        if(answerRepository.countByQuestionIdAndEnrollmentId(answerRequest.questionId, enrollmentId) > 0L) {
            throw AlreadyAnsweredException("Question has already been answered")
        }
    }

}
