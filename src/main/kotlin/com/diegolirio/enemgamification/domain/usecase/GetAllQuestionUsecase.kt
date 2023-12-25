package com.diegolirio.enemgamification.domain.usecase

import com.diegolirio.enemgamification.domain.dataproviders.repository.QuestionRepository
import com.diegolirio.enemgamification.domain.entity.QuestionEntity
import org.springframework.stereotype.Service

@Service
class GetAllQuestionUsecase(
        private val questionRepository: QuestionRepository
) {

    fun get(): List<QuestionEntity> {
        return questionRepository.findAll() //
    }
}