package com.diegolirio.enemgamification.app.entrypoint

import com.diegolirio.enemgamification.EnemGamificationApplication
import com.diegolirio.enemgamification.TestEnemGamificationApplication
import com.diegolirio.enemgamification.app.entrypoint.data.PageResponse
import com.diegolirio.enemgamification.app.entrypoint.data.QuestionRequest
import com.diegolirio.enemgamification.app.entrypoint.data.QuestionResponse
import com.diegolirio.enemgamification.domain.dataproviders.repository.EnrollmentRepository
import com.diegolirio.enemgamification.domain.dataproviders.repository.TestRepository
import com.diegolirio.enemgamification.domain.entity.EnrollmentEntity
import com.diegolirio.enemgamification.domain.entity.TestEntity
import com.diegolirio.enemgamification.domain.usecase.exception.AlreadyAnsweredException
import com.diegolirio.enemgamification.domain.usecase.exception.PageNumberAndPageSizeFormatException
import com.diegolirio.enemgamification.domain.usecase.input.AnswerRequest
import com.diegolirio.enemgamification.domain.usecase.output.AnswerResponse
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.*
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*
import kotlin.collections.HashMap

@SpringBootTest(classes = [EnemGamificationApplication::class], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestEnemGamificationApplication::class)
@ActiveProfiles("test")
class QuestionRestControllerTests {

    @Autowired private lateinit var restTemplate: TestRestTemplate
    @Autowired private lateinit var testRepository: TestRepository
    @Autowired private lateinit var enrollmentRepository: EnrollmentRepository

    @Test
    fun `test get all questions paged`() {
        val testEntity = testRepository.findByDescription("Enem 2024")[0]
        val entity = HttpEntity<String>(headersGetQuestions(testEntity.id!!))

        restTemplate.exchange(
                "${QuestionRestController.PATH}?pageNumber=0&pageSize=10",
                HttpMethod.GET,
                entity,
                //object : ParameterizedTypeReference<List<QuestionResponse>>() {}
                object : ParameterizedTypeReference<PageResponse<QuestionResponse>>() {}
        ).let {
            println(it.body)
            assertEquals(HttpStatus.OK, it.statusCode)
            assertTrue(it.body!!.content.isNotEmpty())
            assertFalse(it.body!!.last)
            assertTrue(it.body!!.first)
            assertFalse(it.body!!.empty)
            assertEquals(5, it.body!!.totalPages)
            assertEquals(50, it.body!!.totalElements)
            assertEquals(10, it.body!!.pageSize)
            assertEquals(0, it.body!!.pageNumber)
        }
    }

    @Test
    fun `test create a test and question, get all questions paged`() {

        val testCreated = testRepository.save(
            TestEntity(
                date = LocalDateTime.of(LocalDate.of(2022, 12, 1), LocalTime.MIDNIGHT),
                description = "Enem Test 2022"
            )
        )

        val request = QuestionRequest(
                number = 1,
                statement = "Qual as cores da bandeira do Brasil?",
                alternativeAnswers = listOf(
                        QuestionRequest.AlternativeAnswer(
                            description = "Verde, Amarelo, Azul e Branco",
                            letter = 'A'
                        )),
                area = "Conhecimentos Gerais",
                testId = testCreated.id!!,
                correctAnswer = 'A'
        )
        postQuestion(request)

        val entity = HttpEntity<String>(headersGetQuestions(testCreated.id!!))

        restTemplate.exchange(
                "${QuestionRestController.PATH}?pageNumber=0&pageSize=10",
                HttpMethod.GET,
                entity,
                //object : ParameterizedTypeReference<List<QuestionResponse>>() {}
                object : ParameterizedTypeReference<PageResponse<QuestionResponse>>() {}
            ).let {
                println(it.body)
                assertEquals(HttpStatus.OK, it.statusCode)
                assertTrue(it.body!!.content.isNotEmpty())
                assertTrue(it.body!!.last)
                assertTrue(it.body!!.first)
                assertFalse(it.body!!.empty)
                assertEquals(1, it.body!!.totalPages)
                assertEquals(1, it.body!!.totalElements)
                assertEquals(10, it.body!!.pageSize)
                assertEquals(0, it.body!!.pageNumber)
                assertEquals(it.body!!.content[0].statement, request.statement)
                assertEquals(it.body!!.content[0].number, request.number)
                assertTrue(it.body!!.content[0].alternativeAnswers!!.isNotEmpty())
                assertNotNull(it.body!!.content[0].testDescription)
            }

    }

    @Test
    fun `test get all questions paged, pageNumber and pageSize being a string, bad request`() {

        val entity = HttpEntity<String>(headersGetQuestions("12344545"))
        restTemplate.exchange(
                "${QuestionRestController.PATH}?pageNumber=XPTO&pageSize=1",
                HttpMethod.GET,
                entity,
                object : ParameterizedTypeReference<ProblemDetail>() {}
        ).let {
            assertEquals(HttpStatus.BAD_REQUEST, it.statusCode)
            assertEquals(PageNumberAndPageSizeFormatException.PROBLEM_DETAIL_TITLE, it.body!!.title)
            assertEquals(PageNumberAndPageSizeFormatException.PROBLEM_DETAIL_DETAIL, it.body!!.detail)
        }

        restTemplate.exchange(
                "${QuestionRestController.PATH}?pageNumber=0&pageSize=Test-With-String",
                HttpMethod.GET,
                entity,
                object : ParameterizedTypeReference<ProblemDetail>() {}
        ).let {
            println(it.body)
            assertEquals(HttpStatus.BAD_REQUEST, it.statusCode)
            assertEquals(PageNumberAndPageSizeFormatException.PROBLEM_DETAIL_TITLE, it.body!!.title)
            assertEquals(PageNumberAndPageSizeFormatException.PROBLEM_DETAIL_DETAIL, it.body!!.detail)
        }
    }

    private fun postQuestion(requestBody: QuestionRequest) {
        restTemplate.postForEntity(
                QuestionRestController.PATH, HttpEntity(requestBody, headersPostAnswer("")),
                Any::class.java
        ).let {
            assertEquals(HttpStatus.CREATED, it.statusCode)
        }
    }

    @Test
    fun `test should request a answer and return a scoring, httpStatus Created`() {

        val testEntity = testRepository.findByDescription("Enem 2024")[0]
        val entity = HttpEntity<String>(headersGetQuestions(testEntity.id!!))

        restTemplate.exchange(
                "${QuestionRestController.PATH}?pageNumber=0&pageSize=10",
                HttpMethod.GET,
                entity,
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

            val enrollment = enrollmentRepository.save(EnrollmentEntity(name = "Inscrito 10", test = testEntity))

            restTemplate.postForEntity(
                    "${QuestionRestController.PATH}/answers", HttpEntity(it, headersPostAnswer(enrollment.id!!)),
                    AnswerResponse::class.java
            ).let { resp ->
                println(resp.body)
                assertEquals(HttpStatus.CREATED, resp.statusCode)
                assertEquals(-5, resp.body!!.scoring)

                val enrollment2 = enrollmentRepository.findById(enrollment.id!!)
                assertTrue(enrollment2.get().scoringLevel.scoringTotal < 0)

            }
        }
    }

    @Test
    fun `test should request a answer that was already requested and return httpStatus UNPROCESSABLE ENTITY`() {
        val testEntity = testRepository.findByDescription("Enem 2024")[0] // TODO It's YOU
        val entity = HttpEntity<String>(headersGetQuestions(testEntity.id!!))
        restTemplate.exchange(
                "${QuestionRestController.PATH}?pageNumber=1&pageSize=10",
                HttpMethod.GET,
                entity,
                object : ParameterizedTypeReference<PageResponse<QuestionResponse>>() {}
        ).let {
            assertEquals(HttpStatus.OK, it.statusCode)
            val answerRequest = AnswerRequest(
                    answer = 'D',
                    questionId = it.body!!.content[8].id!!
            )

            val enrollment = enrollmentRepository.save(EnrollmentEntity(name = "Inscrito 11", test = testEntity))

            restTemplate.postForEntity(
                    "${QuestionRestController.PATH}/answers", HttpEntity(answerRequest, headersPostAnswer(enrollment.id!!)),
                    AnswerResponse::class.java
            ).let { resp ->
                assertEquals(HttpStatus.CREATED, resp.statusCode)
            }
            // request again the same question/answer
            restTemplate.postForEntity(
                    "${QuestionRestController.PATH}/answers", HttpEntity(answerRequest, headersPostAnswer(enrollment.id!!)),
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

        val enrollment = enrollmentRepository.findAll()[0]

        val answerRequest = AnswerRequest(
                answer = 'E',
                questionId = UUID.randomUUID().toString()
        )
        restTemplate.postForEntity(
                "${QuestionRestController.PATH}/answers", HttpEntity(answerRequest, headersPostAnswer(enrollment.id!!)),
                AnswerResponse::class.java
        ).let { resp ->
            assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, resp.statusCode)
        }
    }

    @Test
    fun `test save answer, no enrollmentId header, should return httpStatus BAD REQUEST`() {

        val answerRequest = AnswerRequest(
                answer = 'A',
                questionId = UUID.randomUUID().toString()
        )
        restTemplate.postForEntity(
                "${QuestionRestController.PATH}/answers", HttpEntity(answerRequest, HttpHeaders()),
                AnswerResponse::class.java
        ).let { resp ->
            assertEquals(HttpStatus.BAD_REQUEST, resp.statusCode)
        }
    }

    @Test
    fun `test should answer all questions and check scoring-and-rate, success`() {

        val test = testRepository.findByDescription("Enem 2024")[0]
        val enrollment = enrollmentRepository.save(EnrollmentEntity(name = "Tatiane Santos", test = test))
        val mapQuestions = getMapQuestionsAndRating()

        val entity = HttpEntity<String>(headersGetQuestions(test.id!!))

        for (pageNumber in 0 until 3) {
                restTemplate.exchange(
                        "${QuestionRestController.PATH}?pageNumber=$pageNumber&pageSize=10",
                        HttpMethod.GET,
                        entity,
                        object : ParameterizedTypeReference<PageResponse<QuestionResponse>>() {}
                ).let {
                    assertEquals(HttpStatus.OK, it.statusCode)
                    it.body!!.content.map { question ->
                        Pair(question, answer(question))
                    }
                }.forEach { questionAnswerPair ->
                    restTemplate.postForEntity(
                            "${QuestionRestController.PATH}/answers", HttpEntity(questionAnswerPair.second, headersPostAnswer(enrollment.id!!)),
                            AnswerResponse::class.java
                    ).let { resp ->
                        assertEquals(HttpStatus.CREATED, resp.statusCode)
                        println("Question " + questionAnswerPair.first.number)
                        if(listOf(1,7,9,10,15,16,17,27,30).contains(questionAnswerPair.first.number)) {
                            val enrollmentUpdated = enrollmentRepository.findById(enrollment.id!!).get()
                            val expectation = mapQuestions[questionAnswerPair.first.number]
                            assertEquals(expectation!!.first, enrollmentUpdated.scoringLevel.scoringTotal)
                            assertEquals(expectation.second, enrollmentUpdated.scoringLevel.rating)
                        }
                    }
                }
        }
    }

    private fun getMapQuestionsAndRating(): HashMap<Int, Pair<Int, EnrollmentEntity.RatingEnum>> {
        val map = HashMap<Int, Pair<Int, EnrollmentEntity.RatingEnum>>()
        map[1] = Pair(-5, EnrollmentEntity.RatingEnum.NEWBIE)
        map[7] = Pair(55, EnrollmentEntity.RatingEnum.KNOWLEDGEABLE)
        map[9] = Pair(45, EnrollmentEntity.RatingEnum.NEWBIE)
        map[10] = Pair(55, EnrollmentEntity.RatingEnum.KNOWLEDGEABLE)
        map[15] = Pair(105, EnrollmentEntity.RatingEnum.EXPERT)
        map[16] = Pair(100, EnrollmentEntity.RatingEnum.KNOWLEDGEABLE)
        map[17] = Pair(110, EnrollmentEntity.RatingEnum.EXPERT)
        map[27] = Pair(210, EnrollmentEntity.RatingEnum.MASTER)
        map[30] = Pair(225, EnrollmentEntity.RatingEnum.MASTER)
        return map
    }

    private fun answer(questionResponse: QuestionResponse): AnswerRequest {
        val letter: Char = when (questionResponse.number) {
            1->'A' 2->'C' 3->'B' 4->'A' 5->'C' 6->'B' 7->'A' 8->'C' 9->'C' 10->'A' 11->'D' 12->'D'
            13->'B' 14->'B' 15->'B' 16->'C' 17->'B' 18->'B' 19->'C' 20->'C' 21->'A' 22->'A' 23->'A'
            24->'B' 25->'B' 26->'A' 27->'B' 28->'A' 29->'A' 30->'D'
            else -> 'X'
        }
        return AnswerRequest(
                answer = letter,
                questionId = questionResponse.id!!
        )
    }

    private fun headersPostAnswer(enrollmentId: String): HttpHeaders =
            HttpHeaders()
                .let {
                    it.set("enrollment", enrollmentId)
                    return@let it
                }

    private fun headersGetQuestions(testId: String): HttpHeaders =
            HttpHeaders()
                    .let {
                        it.set("testId", testId)
                        return@let it
                    }

}