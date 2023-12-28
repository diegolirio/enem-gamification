package com.diegolirio.enemgamification.domain.usecase

import com.diegolirio.enemgamification.domain.dataproviders.repository.AnswerRepository
import com.diegolirio.enemgamification.domain.dataproviders.repository.EnrollmentRepository
import com.diegolirio.enemgamification.domain.dataproviders.repository.QuestionRepository
import com.diegolirio.enemgamification.domain.entity.AnswerEntity
import com.diegolirio.enemgamification.domain.entity.EnrollmentEntity
import com.diegolirio.enemgamification.domain.entity.QuestionEntity
import com.diegolirio.enemgamification.domain.entity.TestEntity
import com.diegolirio.enemgamification.domain.usecase.exception.AlreadyAnsweredException
import com.diegolirio.enemgamification.domain.usecase.exception.AnswerOutOfBoundsException
import com.diegolirio.enemgamification.domain.usecase.exception.EnrollmentDoesNotBelongException
import com.diegolirio.enemgamification.domain.usecase.input.AnswerRequest
import com.diegolirio.enemgamification.domain.usecase.output.AnswerResponse
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class SaveAnswerUsecaseTest {
    private lateinit var saveAnswerUsecase: SaveAnswerUsecase
    private lateinit var questionRepository: QuestionRepository
    private lateinit var answerRepository: AnswerRepository
    private lateinit var enrollmentRepository: EnrollmentRepository

    @BeforeEach
    fun setUp() {
        questionRepository = mockk()
        answerRepository = mockk()
        enrollmentRepository = mockk()
        saveAnswerUsecase = SaveAnswerUsecase(questionRepository, answerRepository, enrollmentRepository)
    }

    @Test
    fun `should throw AnswerOutOfBoundsException when answer is not between A and D`() {
        val enrollmentId = "enrollmentId"
        val answerRequest = AnswerRequest("questionId", 'E')
        assertThrows(AnswerOutOfBoundsException::class.java) {
            saveAnswerUsecase.save(enrollmentId, answerRequest)
        }
    }

    @Test
    fun `should throw AlreadyAnsweredException when answer and enrollment have already answered`() {
        val enrollmentId = "enrollmentId"
        val answerRequest = AnswerRequest("questionId", 'C')
        every { answerRepository.countByQuestionIdAndEnrollmentId(answerRequest.questionId, enrollmentId) } returns 1L
        assertThrows(AlreadyAnsweredException::class.java) {
            saveAnswerUsecase.save(enrollmentId, answerRequest)
        }
    }

    @Test
    fun `should throw EnrollmentDoesNotBelongException when enrollment and question do not belong to the same test`() {
        val enrollmentId = "enrollmentId"
        val answerRequest = AnswerRequest("questionId", 'A')
        val enrollment = EnrollmentEntity(id = "enrollmentId", test = TestEntity(id = "testId", date = LocalDateTime.now(), ""))
        val questionEntity = QuestionEntity(id = "questionId", test = TestEntity("differentTestId"), correctAnswer = 'A')
        every { answerRepository.countByQuestionIdAndEnrollmentId(answerRequest.questionId, enrollmentId) } returns 0L
        every { enrollmentRepository.findById(enrollmentId).get() } returns enrollment
        every { questionRepository.findById(answerRequest.questionId).get() } returns questionEntity
        assertThrows(EnrollmentDoesNotBelongException::class.java) {
            saveAnswerUsecase.save(enrollmentId, answerRequest)
        }
    }

    @Test
    fun `should return AnswerResponse with scoring 10 when answer is correct`() {
        val enrollmentId = "enrollmentId"
        val answerRequest = AnswerRequest("questionId", 'A')
        val enrollment = EnrollmentEntity("enrollmentId", test = TestEntity("testId"))
        val questionEntity = QuestionEntity("questionId", test = TestEntity("testId"), correctAnswer = 'A')
        every { answerRepository.countByQuestionIdAndEnrollmentId(answerRequest.questionId, enrollmentId) } returns 0L
        every { enrollmentRepository.findById(enrollmentId).get() } returns enrollment
        every { questionRepository.findById(answerRequest.questionId).get() } returns questionEntity
        every { answerRepository.save(any()) } returns AnswerEntity(question =  questionEntity, answer = 'A', scoring =  10, enrollment = enrollment)
        every { enrollmentRepository.save(any()) } returns enrollment

        val expected = AnswerResponse(10)
        assertEquals(expected, saveAnswerUsecase.save(enrollmentId, answerRequest))
    }

    @Test
    fun `should return AnswerResponse with scoring -5 when answer is incorrect`() {
        val enrollmentId = "enrollmentId"
        val answerRequest = AnswerRequest("questionId", 'B')
        val enrollment = EnrollmentEntity("enrollmentId", test = TestEntity("testId"))
        val questionEntity = QuestionEntity("questionId", test = TestEntity("testId"),  correctAnswer = 'A')
        every { answerRepository.countByQuestionIdAndEnrollmentId(answerRequest.questionId, enrollmentId) } returns 0L
        every { enrollmentRepository.findById(enrollmentId).get() } returns enrollment
        every { questionRepository.findById(answerRequest.questionId).get() } returns questionEntity
        every { answerRepository.save(any()) } returns AnswerEntity(question = questionEntity, answer =  'B', scoring = -5, enrollment = enrollment)
        every { enrollmentRepository.save(any()) } returns enrollment
        val expected = AnswerResponse(-5)
        assertEquals(expected, saveAnswerUsecase.save(enrollmentId, answerRequest))
    }

    @Test
    fun `should update enrollment scoring level and rating KNOWLEDGEABLE`() {
        val enrollmentId = "enrollmentId"
        val answerRequest = AnswerRequest("questionId", 'A')
        val enrollment = EnrollmentEntity(id = "enrollmentId", test = TestEntity("testId"), scoringLevel = EnrollmentEntity.ScoringLevel(45, EnrollmentEntity.RatingEnum.NEWBIE))
        val questionEntity = QuestionEntity("questionId", test = TestEntity("testId"), correctAnswer = 'A', number = 10)
        every { answerRepository.countByQuestionIdAndEnrollmentId(answerRequest.questionId, enrollmentId) } returns 0L
        every { enrollmentRepository.findById(enrollmentId).get() } returns enrollment
        every { questionRepository.findById(answerRequest.questionId).get() } returns questionEntity
        every { answerRepository.save(any()) } returns AnswerEntity(question = questionEntity, answer = 'A', scoring = 10, enrollment = enrollment)
        every { enrollmentRepository.save(any()) } returns enrollment
        saveAnswerUsecase.save(enrollmentId, answerRequest)
        assertEquals(55, enrollment.scoringLevel.scoringTotal)
        assertEquals(EnrollmentEntity.RatingEnum.KNOWLEDGEABLE, enrollment.scoringLevel.rating)
    }

    @Test
    fun `should update enrollment scoring level and rating EXPERT`() {
        val enrollmentId = "enrollmentId"
        val answerRequest = AnswerRequest("questionId", 'A')
        val enrollment = EnrollmentEntity(id = "enrollmentId", test = TestEntity("testId"), scoringLevel = EnrollmentEntity.ScoringLevel(99, EnrollmentEntity.RatingEnum.KNOWLEDGEABLE))
        val questionEntity = QuestionEntity("questionId", test = TestEntity("testId"), correctAnswer = 'A', number = 10)
        every { answerRepository.countByQuestionIdAndEnrollmentId(answerRequest.questionId, enrollmentId) } returns 0L
        every { enrollmentRepository.findById(enrollmentId).get() } returns enrollment
        every { questionRepository.findById(answerRequest.questionId).get() } returns questionEntity
        every { answerRepository.save(any()) } returns AnswerEntity(question = questionEntity, answer = 'A', scoring = 10, enrollment = enrollment)
        every { enrollmentRepository.save(any()) } returns enrollment
        saveAnswerUsecase.save(enrollmentId, answerRequest)
        assertEquals(109, enrollment.scoringLevel.scoringTotal)
        assertEquals(EnrollmentEntity.RatingEnum.EXPERT, enrollment.scoringLevel.rating)
    }

    @Test
    fun `should update enrollment scoring level and rating MASTER`() {
        val enrollmentId = "enrollmentId"
        val answerRequest = AnswerRequest("questionId", 'A')
        val enrollment = EnrollmentEntity(id = "enrollmentId", test = TestEntity("testId"), scoringLevel = EnrollmentEntity.ScoringLevel(191, EnrollmentEntity.RatingEnum.EXPERT))
        val questionEntity = QuestionEntity("questionId", test = TestEntity("testId"), correctAnswer = 'A', number = 10)
        every { answerRepository.countByQuestionIdAndEnrollmentId(answerRequest.questionId, enrollmentId) } returns 0L
        every { enrollmentRepository.findById(enrollmentId).get() } returns enrollment
        every { questionRepository.findById(answerRequest.questionId).get() } returns questionEntity
        every { answerRepository.save(any()) } returns AnswerEntity(question = questionEntity, answer = 'A', scoring = 10, enrollment = enrollment)
        every { enrollmentRepository.save(any()) } returns enrollment
        saveAnswerUsecase.save(enrollmentId, answerRequest)
        assertEquals(201, enrollment.scoringLevel.scoringTotal)
        assertEquals(EnrollmentEntity.RatingEnum.MASTER, enrollment.scoringLevel.rating)
    }
}
