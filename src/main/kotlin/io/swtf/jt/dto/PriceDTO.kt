package io.swtf.jt.dto

import io.swtf.jt.enums.Resource
import java.util.*

data class PriceDTO(val key: String,
                    val resource: Resource,
                    val date: Date)