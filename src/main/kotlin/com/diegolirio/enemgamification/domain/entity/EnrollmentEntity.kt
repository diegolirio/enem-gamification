package com.diegolirio.enemgamification.domain.entity

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document


/**

   Registration

 */

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

//        - Novato: 0 - 50 pontos
//        - Conhecedor: 51 - 100 pontos
//        - Expert: 101 - 200 pontos
//        - Mestre do ENEM: 201+ pontos

        NEWBIE,
        KNOWLEDGEABLE,
        EXPERT,
        MASTER
    }
}
