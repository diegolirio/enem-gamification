package com.diegolirio.enemgamification.app.entrypoint.data

import com.diegolirio.enemgamification.domain.entity.QuestionEntity

data class QuestionResponse(
        var id: String? = null,
        var number: Int? = null,
        var statement: String? = null,
        var alternativeAnswers: List<AlternativeAnswer>? = null,
        var area: String? = null,
        var testDescription: String? = null
) {
    data class AlternativeAnswer(
            var letter: Char? = null,
            var description: String = "",
    )
}

fun QuestionEntity.toResponse(): QuestionResponse =
        QuestionResponse(
                id = this.id,
                number = this.number,
                statement = this.statement,
                alternativeAnswers = this.alternativeAnswers?.map(QuestionEntity.AlternativeAnswer::toResponse),
                area = this.area,
                testDescription = this.test?.description
        )

fun QuestionEntity.AlternativeAnswer.toResponse(): QuestionResponse.AlternativeAnswer =
        QuestionResponse.AlternativeAnswer(
                letter = this.letter,
                description = this.description
        )