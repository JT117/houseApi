package io.swtf.jt.dto

import io.swtf.jt.dao.MeterReading
import io.swtf.jt.enums.ReadingUnit
import io.swtf.jt.enums.Resource
import java.util.*

data class MeterReadingDTO(val key: String, val resource: Resource, val unit: ReadingUnit, val number: Long, val date: Date){

    constructor( meterReading: MeterReading, key: String) : this(key, meterReading.resource, meterReading.unit, meterReading.number, meterReading.date)

}