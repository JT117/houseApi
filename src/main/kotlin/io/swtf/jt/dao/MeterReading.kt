package io.swtf.jt.dao

import io.swtf.jt.dto.MeterReadingDTO
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
        val number: Long,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        val date: Date
){
    constructor(meterReadingDTO: MeterReadingDTO): this(UUID.randomUUID().toString(), meterReadingDTO.resource, meterReadingDTO.unit, meterReadingDTO.number, meterReadingDTO.date)
}

interface MeterReadingRepo : MongoRepository<MeterReading, String> {

    fun findByResourceAndDateBetween(resource: Resource, begin: Date, end: Date): List<MeterReading>

    fun findAllByResource(resource: Resource): List<MeterReading>

    fun findByDateBetween(begin: Date, end: Date): List<MeterReading>
}