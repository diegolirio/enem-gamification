package com.diegolirio.enemgamification.app.entrypoint

import com.diegolirio.enemgamification.EnemGamificationApplication
import com.diegolirio.enemgamification.TestEnemGamificationApplication
import com.diegolirio.enemgamification.app.entrypoint.data.PageResponse
import com.diegolirio.enemgamification.app.entrypoint.data.QuestionRequest
import com.diegolirio.enemgamification.app.entrypoint.data.QuestionResponse
import com.diegolirio.enemgamification.domain.usecase.exception.AlreadyAnsweredException
import com.diegolirio.enemgamification.domain.usecase.exception.PageNumberAndPageSizeFormatException
import com.diegolirio.enemgamification.domain.usecase.input.AnswerRequest
import com.diegolirio.enemgamification.domain.usecase.output.AnswerResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.*
import org.springframework.test.context.ActiveProfiles
import java.util.*

@SpringBootTest(classes = [EnemGamificationApplication::class], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestEnemGamificationApplication::class)
@ActiveProfiles("test")
class QuestionRestControllerTests {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Test
    fun `test get all questions paged`() {

        val request = QuestionRequest(
                number = 1,
                statement = "Qual as cores da bandeira do Brasil?",
                alternativeAnswers = listOf(QuestionRequest.AlternativeAnswer(
                        description = "Verde, Amarelo, Roxo e Branco",
                        letter = 'A'
                )),
                area = "Conhecimentos Gerais"
        )
        postQuestion(request)

        /**
            restTemplate.exchange(
                "",
                HttpMethod.GET,
                null,
                object : ParameterizedTypeReference<List<QuestionResponse>>() {}
            )
         */

        /**
        val headers = HttpHeaders()
        headers.accept = listOf(MediaType.APPLICATION_JSON)
        val entity = HttpEntity<String>(headers)
        val responseEntity: ResponseEntity<List<QuestionResponse>> =
        restTemplate.exchange(QuestionRestController.PATH, HttpMethod.GET, entity, object : ParameterizedTypeReference<List<QuestionResponse>>() {})
         */

        /**
        webClient
            .get()
            .uri(url)
            .retrieve()
            .bodyToMono(object : ParameterizedTypeReference<List<Question>>() {})
            .block() // blocking for simplicity; consider using subscribe() in a reactive context
         */

        restTemplate.getForEntity(
                "${QuestionRestController.PATH}?pageNumber=0&pageSize=1", Any::class.java
        ).let {
            println(it.body)
            assertEquals(HttpStatus.OK, it.statusCode)
        }

        restTemplate.exchange(
                "${QuestionRestController.PATH}?pageNumber=0&pageSize=10",
                HttpMethod.GET,
                null,
                //object : ParameterizedTypeReference<List<QuestionResponse>>() {}
                object : ParameterizedTypeReference<PageResponse<QuestionResponse>>() {}
            ).let {
                println(it.body)
                assertEquals(HttpStatus.OK, it.statusCode)
                assertTrue(it.body!!.content.isNotEmpty())
                assertFalse(it.body!!.last)
                assertTrue(it.body!!.first)
                assertFalse(it.body!!.empty)
                assertEquals(3, it.body!!.totalPages)
                assertEquals(28, it.body!!.totalElements)
                assertEquals(10, it.body!!.pageSize)
                assertEquals(0, it.body!!.pageNumber)
                assertEquals(it.body!!.content[0].statement, request.statement)
                assertEquals(it.body!!.content[0].number, request.number)
                assertTrue(it.body!!.content[0].alternativeAnswers!!.isNotEmpty())
            }

        //        restTemplate.exchange(
        //                QuestionRestController.PATH,
        //                HttpMethod.GET,
        //                null,
        //                object : ParameterizedTypeReference<List<QuestionResponse>>() {}
        //        ).let {
        //            Assertions.assertEquals(HttpStatus.OK, it.statusCode)
        //            Assertions.assertEquals(it.body!!.description, "XPTO")
        //        }

    }

    @Test
    fun `test get all questions paged, pageNumber and pageSize being a string, bad request`() {
        restTemplate.getForEntity(
                "${QuestionRestController.PATH}?pageNumber=XPTO&pageSize=1", ProblemDetail::class.java
        ).let {
            println(it.body)
            assertEquals(HttpStatus.BAD_REQUEST, it.statusCode)
            assertEquals(PageNumberAndPageSizeFormatException.PROBLEM_DETAIL_TITLE, it.body!!.title)
            assertEquals(PageNumberAndPageSizeFormatException.PROBLEM_DETAIL_DETAIL, it.body!!.detail)
        }

        restTemplate.getForEntity(
                "${QuestionRestController.PATH}?pageNumber=0&pageSize=Test-With-String", ProblemDetail::class.java
        ).let {
            println(it.body)
            assertEquals(HttpStatus.BAD_REQUEST, it.statusCode)
            assertEquals(PageNumberAndPageSizeFormatException.PROBLEM_DETAIL_TITLE, it.body!!.title)
            assertEquals(PageNumberAndPageSizeFormatException.PROBLEM_DETAIL_DETAIL, it.body!!.detail)
        }
    }

    private fun postQuestion(requestBody: QuestionRequest) {

//        restTemplate.exchange(
//                QuestionRestController.PATH,
//                HttpMethod.POST,
//                null,
//                object : ParameterizedTypeReference<QuestionResponse>() {}
//        )

//        restTemplate.exchange(
//                url = QuestionRestController.PATH,
//                HttpMethod.POST,
//                HttpEntity(QuestionResponse),
//                QuestionResponse::class.java
//        )

        restTemplate.postForEntity(
                QuestionRestController.PATH, HttpEntity(requestBody, headersCreateConciliation()),
                Any::class.java
        ).let {
            assertEquals(HttpStatus.CREATED, it.statusCode)
        }
    }

//    @Test
//    fun testPostRequest() {
//        val url = "/your-api-endpoint"
//        val requestBody = YourRequestObject(/* provide request data here */)
//
//        val requestEntity: HttpEntity<YourRequestObject> = HttpEntity(requestBody)
//        val responseEntity: ResponseEntity<YourResponseObject> =
//                testRestTemplate.exchange(url, HttpMethod.POST, requestEntity, YourResponseObject::class.java)
//
//        assert(responseEntity.statusCode == HttpStatus.CREATED)
//    }

    @Test
    fun `test should request a answer and return a scoring, httpStatus Created`() {
        restTemplate.exchange(
                "${QuestionRestController.PATH}?pageNumber=0&pageSize=10",
                HttpMethod.GET,
                null,
                object : ParameterizedTypeReference<PageResponse<QuestionResponse>>() {}
        ).let {
            assertEquals(HttpStatus.OK, it.statusCode)
            it.body!!.content[9]
        }.let {
            AnswerRequest(
                    answer = 'C',
                    questionId = it.id!!
            )
        }.let {
            restTemplate.postForEntity(
                    "${QuestionRestController.PATH}/answers", HttpEntity(it, headersCreateConciliation()),
                    AnswerResponse::class.java
            ).let { resp ->
                println(resp.body)
                assertEquals(HttpStatus.CREATED, resp.statusCode)
                assertEquals(-5, resp.body!!.scoring)
            }
        }
    }

    @Test
    fun `test should request a answer that was already requested and return httpStatus UNPROCESSABLE ENTITY`() {
        restTemplate.exchange(
                "${QuestionRestController.PATH}?pageNumber=1&pageSize=10",
                HttpMethod.GET,
                null,
                object : ParameterizedTypeReference<PageResponse<QuestionResponse>>() {}
        ).let {
            assertEquals(HttpStatus.OK, it.statusCode)
            val answerRequest = AnswerRequest(
                    answer = 'D',
                    questionId = it.body!!.content[8].id!!
            )

            restTemplate.postForEntity(
                    "${QuestionRestController.PATH}/answers", HttpEntity(answerRequest, headersCreateConciliation()),
                    AnswerResponse::class.java
            ).let { resp ->
                assertEquals(HttpStatus.CREATED, resp.statusCode)
            }
            // request again the same question/answer
            restTemplate.postForEntity(
                    "${QuestionRestController.PATH}/answers", HttpEntity(answerRequest, headersCreateConciliation()),
                    ProblemDetail::class.java
            ).let { resp ->
                assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, resp.statusCode)
                assertEquals(AlreadyAnsweredException.PROBLEM_DETAIL_TITLE, resp.body!!.title)
                assertEquals(AlreadyAnsweredException.PROBLEM_DETAIL_DETAIL, resp.body!!.detail)
            }
        }
    }

    @Test
    fun `test save answer out of bounds, from A to D, should return httpStatus UNPROCESSABLE ENTITY`() {
        val answerRequest = AnswerRequest(
                answer = 'E',
                questionId = UUID.randomUUID().toString()
        )
        restTemplate.postForEntity(
                "${QuestionRestController.PATH}/answers", HttpEntity(answerRequest, headersCreateConciliation()),
                AnswerResponse::class.java
        ).let { resp ->
            assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, resp.statusCode)
        }
    }

    private fun headersCreateConciliation(): HttpHeaders =
            HttpHeaders()
                    .let {
                        it.set("user", "ddamacena")
                        return@let it
                    }

}