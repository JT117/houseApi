package io.swtf.jt.dto

import io.swtf.jt.enums.ReadingUnit
import io.swtf.jt.enums.Resource
import java.util.*

class MeterReadingDTO(val key: String, val resource: Resource, val unit: ReadingUnit, val date: Date){
    override fun toString(): String {
        return "MeterReadingDTO(key='$key', resource=$resource, unit=$unit, date=$date)"
    }
}