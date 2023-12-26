package com.diegolirio.enemgamification.domain.entity

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("questions")
data class QuestionEntity(
        @Id var id: ObjectId? = null,
        var number: Int? = null,
        var statement: String? = null,
        var alternativeAnswers: List<AlternativeAnswer>? = null,
        var area: String? = null,
        var correctAnswer: Char? = null
) {
    data class AlternativeAnswer(
            var letter: Char? = null,
            var description: String = "",
    )
}
