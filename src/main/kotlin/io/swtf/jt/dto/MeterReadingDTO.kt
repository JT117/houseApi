package io.swtf.jt.dto

import io.swtf.jt.dao.MeterReading
import io.swtf.jt.enums.ReadingUnit
import io.swtf.jt.enums.Resource
import java.util.*

data class MeterReadingDTO(val resource: Resource, val unit: ReadingUnit, var number: Long, val date: Date, val isCumulative: Boolean = false) {

    constructor(meterReading: MeterReading, isCumulative: Boolean) : this(meterReading.resource, meterReading.unit, meterReading.number, meterReading.date, isCumulative)

}