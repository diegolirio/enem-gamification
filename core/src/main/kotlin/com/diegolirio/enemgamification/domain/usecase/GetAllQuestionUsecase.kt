package com.diegolirio.enemgamification.domain.usecase

import com.diegolirio.enemgamification.domain.dataproviders.repository.QuestionRepository
import com.diegolirio.enemgamification.domain.entity.QuestionEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class GetAllQuestionUsecase(
        private val questionRepository: QuestionRepository
) {

    fun get(testId: String, pageNumber: Int, pageSize: Int): Page<QuestionEntity>  {
        return getSort("number", Sort.Direction.ASC.name).let {
            questionRepository.findByTestId(testId, PageRequest.of(pageNumber, pageSize, it))
        }
    }

    private fun getSort(sortBy: String, sortDirection: String) =
            if(sortDirection.equals(Sort.Direction.ASC.name, false)) Sort.by(sortBy).ascending() else
                Sort.by(sortBy).descending()
}