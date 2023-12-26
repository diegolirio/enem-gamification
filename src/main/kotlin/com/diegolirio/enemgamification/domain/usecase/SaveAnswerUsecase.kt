package com.diegolirio.enemgamification.domain.usecase

import com.diegolirio.enemgamification.domain.dataproviders.repository.AnswerRepository
import com.diegolirio.enemgamification.domain.dataproviders.repository.QuestionRepository
import com.diegolirio.enemgamification.domain.dataproviders.repository.ScoringLevelRepository
import com.diegolirio.enemgamification.domain.entity.AnswerEntity
import com.diegolirio.enemgamification.domain.entity.EnrollmentEntity
import com.diegolirio.enemgamification.domain.usecase.exception.AlreadyAnsweredException
import com.diegolirio.enemgamification.domain.usecase.exception.AnswerOutOfBoundsException
import com.diegolirio.enemgamification.domain.usecase.input.AnswerRequest
import com.diegolirio.enemgamification.domain.usecase.output.AnswerResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SaveAnswerUsecase(
        private val questionRepository: QuestionRepository,
        private val answerRepository: AnswerRepository,
        private val scoringLevelRepository: ScoringLevelRepository
) {

    @Transactional
    fun save(answerRequest: AnswerRequest) : AnswerResponse {

        // TODO
        //   5. Create a Enrollment and I should pass the enrollment.id on header and validate if exists on Database
        //   6. No sistema de Gamification devera ter uma Entidade que ira gravar a soma da pontuacao e classificando o nivel
        //   7. Analisar criar Test/Matricula para rankear os candidatos
        //   4. Criar endpoint Limpar Base
        //   5. Criar endpoint Inserir Base confomrme o JSON

        checkConstraints(answerRequest)

        val questionEntity = questionRepository.findById(answerRequest.questionId).get()
        val scoring = if (questionEntity.correctAnswer == answerRequest.answer) 10 else -5

        return AnswerEntity(
                question = questionEntity,
                answer = answerRequest.answer,
                scoring = scoring,
        ).let {
            AnswerResponse(scoring = answerRepository.save(it).scoring)
        }.also {
            // TODO 6
            scoringLevelRepository.findAll()
            val a = EnrollmentEntity()
        }

    }

    private fun checkConstraints(answerRequest: AnswerRequest) {
        if (answerRequest.answer !in 'A'..'D') {
            throw AnswerOutOfBoundsException()
        }
        if(answerRepository.countByQuestionId(answerRequest.questionId) > 0L) {
            throw AlreadyAnsweredException("Question has already been answered")
        }
    }

}
