package io.swtf.jt

import io.swtf.jt.dao.MeterReading
import io.swtf.jt.dao.MeterReadingRepo
import io.swtf.jt.dao.Price
import io.swtf.jt.dao.PriceRepo
import io.swtf.jt.dto.MeterReadingDTO
import io.swtf.jt.dto.PriceDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.util.*

@RestController
@EnableAutoConfiguration
@SpringBootApplication
open class HouseRestApi {

    @Autowired
    private lateinit var meterReadingRepo: MeterReadingRepo

    @Autowired
    private lateinit var priceRepo: PriceRepo

    @Value("\${API_KEY}")
    private lateinit var API_KEY: String

    /**
     * Health Check
     */
    @RequestMapping(value = ["/healthCheck"], method = [(RequestMethod.GET)])
    internal fun healthCheck(@RequestParam(value = "key") key: String) : ResponseEntity<Any>{
        return when(API_KEY){
            key -> ResponseEntity(HttpStatus.OK)
            else -> ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    /**
     * Insert a new meter reading in the database
     */
    @RequestMapping(value = ["/meterReading"], method = [(RequestMethod.POST)])
    internal fun insertMeterReading(@RequestBody meterReadingDTO: MeterReadingDTO): ResponseEntity<MeterReading> {
        return when (API_KEY) {
            meterReadingDTO.key -> {
                meterReadingRepo.save(MeterReading(unit = meterReadingDTO.unit, resource = meterReadingDTO.resource, date = meterReadingDTO.date, id = UUID.randomUUID().toString()))
                ResponseEntity(HttpStatus.OK)
            }
            else -> ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    /**
     * Insert a new price in the database
     */
    @RequestMapping(value = ["/price"], method = [(RequestMethod.POST)])
    internal fun insertPrice(@RequestBody priceDTO: PriceDTO): ResponseEntity<MeterReading> {
        return when (API_KEY) {
            priceDTO.key -> {
                priceRepo.save(Price(resource = priceDTO.resource, date = priceDTO.date, id = UUID.randomUUID().toString()))
                ResponseEntity(HttpStatus.OK)
            }
            else -> ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    /**
     * Retrieve all the meter reading from the database
     */
    @RequestMapping(value = ["/meterReadings"], method = [(RequestMethod.GET)])
    internal fun getAllMeterReading(@RequestParam(value = "key", defaultValue = "unknow") key: String): ResponseEntity<List<MeterReading>> {
        return when (API_KEY) {
            key -> {
                ResponseEntity(meterReadingRepo.findAll(), HttpStatus.OK)
            }
            else -> ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    /**
     * Retrieve all the prices from the database
     */
    @RequestMapping(value = ["/prices"], method = [(RequestMethod.GET)])
    internal fun getAllPrices(@RequestParam(value = "key", defaultValue = "unknow") key: String): ResponseEntity<List<Price>> {
        return when (API_KEY) {
            key -> {
                ResponseEntity(priceRepo.findAll(), HttpStatus.OK)
            }
            else -> ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    /**
     * Retrieve all the meter reading from the database between two dates
     */
    @RequestMapping(value = ["/meterReadingsBetween"], method = [(RequestMethod.GET)])
    internal fun getAllMeterReadingBetween(@RequestParam(value = "key", defaultValue = "unknow") key: String,
                                           @RequestParam(value = "begin", required = true) begin: Date,
                                           @RequestParam(value = "end", required = true) end: Date): ResponseEntity<List<MeterReading>> {
        return when (API_KEY) {
            key -> {
                ResponseEntity(meterReadingRepo.findByDateBetween(begin, end), HttpStatus.OK)
            }
            else -> ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    /**
     * Retrieve all the prices from the database between two dates
     */
    @RequestMapping(value = ["/pricesBetween"], method = [(RequestMethod.GET)])
    internal fun getAllPriceBetween(@RequestParam(value = "key", defaultValue = "unknow") key: String,
                                           @RequestParam(value = "begin", required = true) begin: Date,
                                           @RequestParam(value = "end", required = true) end: Date): ResponseEntity<List<Price>> {
        return when (API_KEY) {
            key -> {
                ResponseEntity(priceRepo.findByDateBetween(begin, end), HttpStatus.OK)
            }
            else -> ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }


    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(HouseRestApi::class.java, *args)
        }
    }

}