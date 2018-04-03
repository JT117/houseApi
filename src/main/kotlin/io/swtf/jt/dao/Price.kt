package io.swtf.jt.dao

import io.swtf.jt.enums.Resource
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.format.annotation.DateTimeFormat
import java.util.*

data class Price(
        @Id
        val id: String,
        val resource: Resource,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        val date: Date
)

interface PriceRepo : MongoRepository<Price, String> {

    fun findByDateBetween( begin: Date, end: Date): List<Price>

}