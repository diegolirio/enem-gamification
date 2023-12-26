package com.diegolirio.enemgamification.domain.entity

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document

@Document("answers")
data class AnswerEntity(
        @Id val id: ObjectId? = null,
        @DBRef val question: QuestionEntity? = null,
        val answer: Char? = null,
        val scoring: Int = 0
)