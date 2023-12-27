package com.diegolirio.enemgamification.domain.usecase

import com.diegolirio.enemgamification.app.entrypoint.data.QuestionRequest
import com.diegolirio.enemgamification.app.entrypoint.data.toEntity
import com.diegolirio.enemgamification.domain.entity.QuestionEntity
import com.diegolirio.enemgamification.domain.dataproviders.repository.QuestionRepository
import com.diegolirio.enemgamification.domain.dataproviders.repository.TestRepository
import org.springframework.stereotype.Service

@Service
class CreateQuestionUsecase(
        private val questionRepository: QuestionRepository,
        private val testRepository: TestRepository
) {

    fun exec(question: QuestionRequest) : QuestionEntity {
        // TODO if test does not exist ?
        val testEntity = testRepository.findById(question.testId).get()
        val questionEntity = question.toEntity(testEntity)
        return questionRepository.save(questionEntity)
    }


}
