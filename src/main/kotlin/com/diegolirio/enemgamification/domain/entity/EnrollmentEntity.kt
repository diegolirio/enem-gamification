package com.diegolirio.enemgamification.domain.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document

@Document("enrollments")
data class EnrollmentEntity(
        @Id var id: String? = null,
        var name: String? = null,
        var scoringLevel: ScoringLevel = ScoringLevel(),
        @DBRef var test: TestEntity? = null
) {

    data class ScoringLevel(
            var scoringTotal: Int = 0,
            var rating: RatingEnum = RatingEnum.NEWBIE
    )

    enum class RatingEnum {
        NEWBIE,
        KNOWLEDGEABLE,
        EXPERT,
        MASTER
    }
}
