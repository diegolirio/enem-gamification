package com.diegolirio.enemgamification.domain.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document("tests")
data class TestEntity(
        @Id var id: String? = null,
        var date: LocalDateTime? = null,
        var description: String? = null
)