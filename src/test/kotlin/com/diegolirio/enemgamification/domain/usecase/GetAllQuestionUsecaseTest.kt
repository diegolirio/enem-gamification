package com.diegolirio.enemgamification.domain.usecase
//
//import com.diegolirio.enemgamification.domain.dataproviders.repository.QuestionRepository
//import com.diegolirio.enemgamification.domain.entity.QuestionEntity
//import org.junit.jupiter.api.Assertions.assertEquals
//import org.junit.jupiter.api.Test
//import org.junit.jupiter.api.extension.ExtendWith
//import org.mockito.InjectMocks
//import org.mockito.Mock
//import org.mockito.Mockito.*
//import org.mockito.junit.jupiter.MockitoExtension
//import org.springframework.data.domain.Page
//import org.springframework.data.domain.PageImpl
//import org.springframework.data.domain.PageRequest
//import org.springframework.data.domain.Sort
//import org.springframework.test.context.junit.jupiter.SpringExtension
//
//@ExtendWith(MockitoExtension::class, SpringExtension::class)
//class GetAllQuestionUsecaseTest {
//
//    @Mock
//    private lateinit var questionRepository: QuestionRepository
//
//    @InjectMocks
//    private lateinit var getAllQuestionUsecase: GetAllQuestionUsecase
//
//
//    @Test
//    fun `test get method`() {
//
//        val testId = "testId"
//        val pageNumber = 0
//        val pageSize = 10
//        val mockSort = Sort.by("number").ascending()
//        val mockPageRequest = PageRequest.of(pageNumber, pageSize, mockSort)
//        val mockQuestionList = listOf(QuestionEntity()) // TODO
//        val mockPage: Page<QuestionEntity> = PageImpl(mockQuestionList, mockPageRequest, 1)
//
//        //`when`(questionRepository.findByTestId(any(), any())).thenReturn(mockPage)
//        `when`(questionRepository.findByTestId(eq(testId), eq(mockPageRequest))).thenReturn(mockPage)
//
//        // Then
//        val result: Page<QuestionEntity> = getAllQuestionUsecase.get(testId, pageNumber, pageSize)
//
//        // Verify
//        verify(questionRepository).findByTestId(testId, mockPageRequest)
//        assertEquals(mockPage, result)
//    }
//
//    // Add additional tests as needed
//}
//

import com.diegolirio.enemgamification.domain.dataproviders.repository.QuestionRepository
import com.diegolirio.enemgamification.domain.entity.QuestionEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import java.util.UUID

class GetAllQuestionUsecaseTest {
    private val questionRepository = mock(QuestionRepository::class.java)
    private val getAllQuestionUsecase = GetAllQuestionUsecase(questionRepository)

    @Test
    fun `test get method returns only one item`() {
        val testId = UUID.randomUUID().toString()
        val pageNumber = 0
        val pageSize = 1
        val expected = PageImpl(listOf(QuestionEntity()))
        `when`(questionRepository.findByTestId(testId, PageRequest.of(pageNumber, pageSize, getSort("number", Sort.Direction.ASC.name)))).thenReturn(expected)
        val actual = getAllQuestionUsecase.get(testId, pageNumber, pageSize)

        verify(questionRepository).findByTestId(testId, PageRequest.of(pageNumber, pageSize, getSort("number", Sort.Direction.ASC.name)))

        assertEquals(expected, actual)
        assertEquals(1, actual.content.size)
    }

    private fun getSort(sortBy: String, sortDirection: String) =
            if(sortDirection.equals(Sort.Direction.ASC.name, false)) Sort.by(sortBy).ascending() else
                Sort.by(sortBy).descending()
}
