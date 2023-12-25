package com.diegolirio.enemgamification.app.entrypoint

import com.diegolirio.enemgamification.EnemGamificationApplication
import com.diegolirio.enemgamification.TestEnemGamificationApplication
import com.diegolirio.enemgamification.app.entrypoint.data.QuestionRequest
import com.diegolirio.enemgamification.domain.entity.QuestionEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles

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
                description = "Qual as cores da bandeira do Brasil?",
                alternativeAnswers = listOf(QuestionRequest.AlternativeAnswer(
                        description = "Verde, Amarelo, Roxo e Branco",
                        letter = 'A'
                ))
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


        //restTemplate.getForEntity(QuestionRestController.PATH, List::class.java as Class<Array<QuestionEntity>>)
        restTemplate.exchange(
                QuestionRestController.PATH,
                HttpMethod.GET,
                null,
                object : ParameterizedTypeReference<List<QuestionEntity>>() {}
            ).let {
                println(it.body)
                assertEquals(HttpStatus.OK, it.statusCode)
                assertTrue(it.body!!.isNotEmpty())
                assertEquals(it.body!![0].description, request.description)
                assertEquals(it.body!![0].number, request.number)
                assertTrue(it.body!![0].alternativeAnswers!!.isNotEmpty())
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
//                HttpEntity(questionEntity),
//                QuestionEntity::class.java
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

    private fun headersCreateConciliation(): HttpHeaders =
            HttpHeaders()
                    .let {
                        it.set("user", "ddamacena")
                        return@let it
                    }

}