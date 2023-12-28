package com.diegolirio.enemgamification.domain.dataproviders.repository

import com.diegolirio.enemgamification.domain.entity.TestEntity
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface TestRepository : MongoRepository<TestEntity, String> {

    fun findByDescription(description: String): List<TestEntity>

}
