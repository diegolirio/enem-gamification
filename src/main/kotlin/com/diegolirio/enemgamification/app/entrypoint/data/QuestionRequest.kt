package com.diegolirio.enemgamification.app.entrypoint.data

import com.diegolirio.enemgamification.domain.entity.QuestionEntity
import com.diegolirio.enemgamification.domain.entity.TestEntity

data class QuestionRequest(
    val number: Int,
    val statement: String,
    var alternativeAnswers: List<AlternativeAnswer>? = null,
    var area: String,
    var testId: String
) {
        data class AlternativeAnswer(
                var description: String,
                var letter: Char
        )
}

fun QuestionRequest.toEntity(testEntity: TestEntity): QuestionEntity =
        QuestionEntity(
                number = this.number,
                statement = this.statement,
                alternativeAnswers = this.alternativeAnswers?.map(QuestionRequest.AlternativeAnswer::toEntity),
                area = this.area,
                test = testEntity
        )

fun QuestionRequest.AlternativeAnswer.toEntity(): QuestionEntity.AlternativeAnswer =
        QuestionEntity.AlternativeAnswer(
                description = this.description,
                letter = this.letter
        )