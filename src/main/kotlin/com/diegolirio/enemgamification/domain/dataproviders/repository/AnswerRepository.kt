package com.diegolirio.enemgamification.domain.dataproviders.repository

import com.diegolirio.enemgamification.domain.entity.AnswerEntity
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface AnswerRepository : MongoRepository<AnswerEntity, String> {

    fun countByQuestionIdAndEnrollmentId(questionId: String, enrollmentId: String): Long

}
