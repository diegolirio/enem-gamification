package com.diegolirio.enemgamification.app.entrypoint.data

import com.diegolirio.enemgamification.domain.entity.QuestionEntity

data class QuestionRequest(
    val number: Int,
    val statement: String,
    var alternativeAnswers: List<AlternativeAnswer>? = null,
    var area: String
) {
        data class AlternativeAnswer(
                var description: String,
                var letter: Char
        )
}

fun QuestionRequest.toEntity(): QuestionEntity =
        QuestionEntity(
                number = this.number,
                statement = this.statement,
                alternativeAnswers = this.alternativeAnswers?.map(QuestionRequest.AlternativeAnswer::toEntity),
                area = this.area
        )

fun QuestionRequest.AlternativeAnswer.toEntity(): QuestionEntity.AlternativeAnswer =
        QuestionEntity.AlternativeAnswer(
                description = this.description,
                letter = this.letter
        )