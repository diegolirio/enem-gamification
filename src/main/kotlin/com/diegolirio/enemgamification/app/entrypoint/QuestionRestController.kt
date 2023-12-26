package com.diegolirio.enemgamification.app.entrypoint

import com.diegolirio.enemgamification.app.entrypoint.data.*
import com.diegolirio.enemgamification.domain.entity.AnswerEntity
import com.diegolirio.enemgamification.domain.usecase.exception.PageNumberAndPageSizeFormatException
import com.diegolirio.enemgamification.domain.entity.QuestionEntity
import com.diegolirio.enemgamification.domain.usecase.CreateQuestionUsecase
import com.diegolirio.enemgamification.domain.usecase.GetAllQuestionUsecase
import com.diegolirio.enemgamification.domain.usecase.SaveAnswerUsecase
import com.diegolirio.enemgamification.domain.usecase.input.AnswerRequest
import com.diegolirio.enemgamification.domain.usecase.output.AnswerResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(QuestionRestController.PATH)
class QuestionRestController(
        private val getAllQuestion: GetAllQuestionUsecase,
        private val createQuestionUsecase: CreateQuestionUsecase,
        private val saveAnswerUsecase: SaveAnswerUsecase
) {

    @GetMapping
    fun getAllQuestions(
            @RequestParam("pageNumber") pageNumber: String = "0",
            @RequestParam("pageSize") pageSize: String = "10"
    ): PageResponse<QuestionResponse> {
        try {
            Pair(pageNumber.toInt(), pageSize.toInt())
        } catch (e: NumberFormatException) {
            throw PageNumberAndPageSizeFormatException("It is not a number")
        }.let {
            return getAllQuestion.get(it.first, it.second).toResponse()
        }
    }

    @PostMapping("/answers")
    @ResponseStatus(HttpStatus.CREATED)
    fun saveAnswers(@RequestBody answer: AnswerRequest): AnswerResponse {
        return saveAnswerUsecase.save(answer)
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