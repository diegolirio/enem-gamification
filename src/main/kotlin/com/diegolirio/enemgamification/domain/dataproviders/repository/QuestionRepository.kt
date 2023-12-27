package com.diegolirio.enemgamification.domain.dataproviders.repository

import com.diegolirio.enemgamification.domain.entity.QuestionEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface QuestionRepository : MongoRepository<QuestionEntity, String> {

    fun findByTestId(testId: String, pageable: PageRequest): Page<QuestionEntity>

}
