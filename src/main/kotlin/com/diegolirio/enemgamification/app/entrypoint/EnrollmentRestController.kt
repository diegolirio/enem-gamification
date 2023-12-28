package com.diegolirio.enemgamification.app.entrypoint

import com.diegolirio.enemgamification.domain.dataproviders.repository.EnrollmentRepository
import com.diegolirio.enemgamification.domain.dataproviders.repository.TestRepository
import com.diegolirio.enemgamification.domain.entity.EnrollmentEntity
import com.diegolirio.enemgamification.domain.entity.TestEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/enrollments")
class EnrollmentRestController(
        private val enrollmentRepository: EnrollmentRepository
) {

    @GetMapping
    fun getList(): MutableList<EnrollmentEntity> {
        return enrollmentRepository.findAll()
    }

}