package com.diegolirio.enemgamification.domain.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("questions")
data class QuestionEntity(
        @Id var id: String? = null,
        var number: Int? = null,
        var statement: String? = null,
        var alternativeAnswers: List<AlternativeAnswer>? = null,
        var area: String? = null
) {
    data class AlternativeAnswer(
            var description: String = "",
            var letter: Char? = null
    )
}
