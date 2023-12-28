package com.diegolirio.enemgamification.app.config

import com.diegolirio.enemgamification.domain.entity.QuestionEntity
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource

@Configuration
class QuestionsConfig {

    @Bean
    fun questions(): List<QuestionEntity> {
        val objectMapper = ObjectMapper()
        val questionsJson = ClassPathResource("questions.json").inputStream
        return objectMapper.readValue(questionsJson)
    }

}