package io.swtf.jt.dao

import io.swtf.jt.enums.ReadingUnit
import io.swtf.jt.enums.Resource
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.format.annotation.DateTimeFormat
import java.util.*

data class MeterReading(
        @Id
        val id: String,
        val resource: Resource,
        val unit: ReadingUnit,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        val date: Date
)

interface MeterReadingRepo : MongoRepository<MeterReading, String> {

    fun findByResourceAndDateBetween(resource: Resource, begin: Date, end: Date): List<MeterReading>

    fun findAllByResource(resource: Resource): List<MeterReading>

    fun findByDateBetween(begin: Date, end: Date): List<MeterReading>
}