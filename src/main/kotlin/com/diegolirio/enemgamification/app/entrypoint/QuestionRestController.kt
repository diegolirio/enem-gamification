package com.diegolirio.enemgamification.app.entrypoint

import com.diegolirio.enemgamification.app.entrypoint.data.QuestionRequest
import com.diegolirio.enemgamification.app.entrypoint.data.toEntity
import com.diegolirio.enemgamification.domain.entity.QuestionEntity
import com.diegolirio.enemgamification.domain.usecase.CreateQuestionUsecase
import com.diegolirio.enemgamification.domain.usecase.GetAllQuestionUsecase
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(QuestionRestController.PATH)
class QuestionRestController(
        private val getAllQuestion: GetAllQuestionUsecase,
        private val createQuestionUsecase: CreateQuestionUsecase
) {

    @GetMapping
    fun getAll(): List<QuestionEntity> {
        return getAllQuestion.get()
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createQuestion(@RequestBody questionRequest: QuestionRequest) {
        createQuestionUsecase.exec(questionRequest.toEntity())
    }

    companion object {
        const val PATH: String = "/v1/questions"
    }
}