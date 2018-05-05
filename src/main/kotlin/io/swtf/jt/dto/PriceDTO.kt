package io.swtf.jt.dto

import io.swtf.jt.dao.Price
import io.swtf.jt.enums.Resource
import java.util.*

data class PriceDTO(val key: String,
                    val resource: Resource,
                    val date: Date,
                    val number: Long){

    constructor(key: String, price: Price): this(key, price.resource, price.date, price.number)
}