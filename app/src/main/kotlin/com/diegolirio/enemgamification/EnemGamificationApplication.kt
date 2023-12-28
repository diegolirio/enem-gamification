package com.diegolirio.enemgamification

import com.diegolirio.enemgamification.domain.dataproviders.repository.EnrollmentRepository
import com.diegolirio.enemgamification.domain.dataproviders.repository.QuestionRepository
import com.diegolirio.enemgamification.domain.dataproviders.repository.TestRepository
import com.diegolirio.enemgamification.domain.entity.EnrollmentEntity
import com.diegolirio.enemgamification.domain.entity.QuestionEntity
import com.diegolirio.enemgamification.domain.entity.TestEntity
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
import java.time.LocalDateTime

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
        return MongoDBContainer(DockerImageName.parse("mongo:4.4.26"))
    }
}

@Component
class MyCommandLineRunner(
        private val questionRepository: QuestionRepository,
        private val testRepository: TestRepository,
        private val listQuestions: List<QuestionEntity>,
        private val enrollmentRepository: EnrollmentRepository
) : CommandLineRunner {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun run(vararg args: String) {
        log.info("Executing CommandLineRunner")
        val testCreated = testRepository.save(TestEntity(date = LocalDateTime.now().plusDays(10), description = "Enem 2024"))
        listQuestions.forEach { it.test = testCreated }
        log.info("Inserting questions) ...")
        questionRepository.saveAll(listQuestions)
        log.info("${listQuestions.size} inserted) !")
        log.info("enrollment created => ${enrollmentRepository.save(EnrollmentEntity(name="Diego Lirio", test = testCreated))}")
        log.info("CommandLineRunner finished")
    }
}