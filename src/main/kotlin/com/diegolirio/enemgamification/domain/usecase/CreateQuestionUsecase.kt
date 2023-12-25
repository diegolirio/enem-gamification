package com.diegolirio.enemgamification.domain.usecase

import com.diegolirio.enemgamification.domain.entity.QuestionEntity
import com.diegolirio.enemgamification.domain.dataproviders.repository.QuestionRepository
import org.springframework.stereotype.Service

@Service
class CreateQuestionUsecase(
        private val questionRepository: QuestionRepository
) {

    fun exec(questionEntity: QuestionEntity) : QuestionEntity {
        return questionRepository.save(questionEntity)
    }


}
