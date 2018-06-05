package io.swtf.jt.dto

import io.swtf.jt.dao.Price
import io.swtf.jt.enums.Resource
import java.util.*

data class PriceDTO(val resource: Resource,
                    val date: Date,
                    val price: Long,
                    val fixedRate: Long) {

    constructor(price: Price) : this(price.resource, price.date, price.price, price.fixedRate)
}