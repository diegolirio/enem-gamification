package com.diegolirio.enemgamification.domain.dataproviders.repository

import com.diegolirio.enemgamification.domain.entity.EnrollmentEntity
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface EnrollmentRepository : MongoRepository<EnrollmentEntity, String> {

}
