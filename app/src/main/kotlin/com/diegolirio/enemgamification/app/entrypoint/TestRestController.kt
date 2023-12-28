package com.diegolirio.enemgamification.app.entrypoint

import com.diegolirio.enemgamification.domain.dataproviders.repository.TestRepository
import com.diegolirio.enemgamification.domain.entity.TestEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/tests")
class TestRestController(
        private val testRepository: TestRepository
) {

    @GetMapping
    fun getList(): MutableList<TestEntity> {
        return testRepository.findAll()
    }

}