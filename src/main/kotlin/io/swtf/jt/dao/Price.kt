package io.swtf.jt.dao

import io.swtf.jt.dto.PriceDTO
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
        val date: Date,
        val price: Long,
        val fixedRate: Long
) {
    constructor(priceDTO: PriceDTO) : this(id = UUID.randomUUID().toString(), resource = priceDTO.resource, date = priceDTO.date, price = priceDTO.price, fixedRate = priceDTO.fixedRate)

    fun forConso(consomation: Long): Long = consomation * price + fixedRate
}

interface PriceRepo : MongoRepository<Price, String> {

    fun findByDateBetween(begin: Date, end: Date): List<Price>

}