package com.diegolirio.enemgamification

import org.springframework.boot.fromApplication
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.boot.with
import org.springframework.context.annotation.Bean
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.utility.DockerImageName

@TestConfiguration(proxyBeanMethods = false)
class TestEnemGamificationApplication {

	@Bean
	@ServiceConnection
	fun kafkaContainer(): KafkaContainer {
		return KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"))
	}

	@Bean
	@ServiceConnection
	fun mongoDbContainer(): MongoDBContainer {
		return MongoDBContainer(DockerImageName.parse("mongo:latest"))
	}

//	@Container
//	var mongoDBContainer = MongoDBContainer("mongo:latest").withExposedPorts(27017)

//	@Bean
//	fun mongoDbContainer(): MongoDBContainer {
//		val container = MongoDBContainer(DockerImageName.parse("mongo:latest"))
//		container.start()
//		return container
//	}
//

//	@Bean
//	fun mongoConnectionString(mongoDbContainer: MongoDBContainer): String {
//		return "mongodb://${mongoDbContainer.host}:${mongoDbContainer.getMappedPort(27017)}/enemdb"
//	}

}

fun main(args: Array<String>) {
	fromApplication<EnemGamificationApplication>().with(TestEnemGamificationApplication::class).run(*args)
}
