package com.diegolirio.enemgamification

import com.diegolirio.enemgamification.domain.dataproviders.repository.QuestionRepository
import com.diegolirio.enemgamification.domain.entity.QuestionEntity
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.utility.DockerImageName

@SpringBootApplication
class EnemGamificationApplication

fun main(args: Array<String>) {
    runApplication<EnemGamificationApplication>(*args)
}

@Configuration
@Profile(value =  ["local", "dev", "default"])
class TestContainersConfigurationApplication {

    @Bean
    @ServiceConnection
    fun mongoDbContainer(): MongoDBContainer {
        return MongoDBContainer(DockerImageName.parse("mongo:latest"))
    }
}

@Component
class MyCommandLineRunner(
        private val questionRepository: QuestionRepository,
        private val listQuestions: List<QuestionEntity>
) : CommandLineRunner {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun run(vararg args: String) {
        log.info("Executing CommandLineRunner (inserting questions) ...")
        questionRepository.saveAll(listQuestions)
        log.info("CommandLineRunner finished (${listQuestions.size} inserted) !")
    }
}