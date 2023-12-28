package com.diegolirio.enemgamification.domain.usecase.input

data class AnswerRequest(
        val questionId: String,
        val answer: Char
)
