package io.swtf.jt

import io.swtf.jt.dao.MeterReading
import io.swtf.jt.dao.MeterReadingRepo
import io.swtf.jt.dao.Price
import io.swtf.jt.dao.PriceRepo
import io.swtf.jt.dto.GraphData
import io.swtf.jt.dto.MeterReadingDTO
import io.swtf.jt.dto.PriceDTO
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.Banner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.format.TextStyle
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
     * Insert a new meter reading in the database
     */
    @RequestMapping(value = ["/meterReading"], method = [(RequestMethod.POST)])
    internal fun insertMeterReading(@RequestBody meterReadingDTO: MeterReadingDTO): ResponseEntity<MeterReading> {
        return when (API_KEY) {
            meterReadingDTO.key -> {
                LOGGER.info("insertMeterReading - Received meter reading to create: $meterReadingDTO")
                val toSave = MeterReading(meterReadingDTO)
                val meterReading = meterReadingRepo.save(toSave)

                if( toSave == meterReading){
                    LOGGER.info("insertMeterReading - Meter reading correctly inserted in the database with id: ${meterReading.id}")
                    ResponseEntity(HttpStatus.OK)
                }else{
                    LOGGER.warn("insertMeterReading - Meter reading NOT inserted")
                    ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
                }
            }
            else -> ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    /**
     * Insert a new price in the database
     */
    @RequestMapping(value = ["/price"], method = [(RequestMethod.POST)])
    internal fun insertPrice(@RequestBody priceDTO: PriceDTO): ResponseEntity<HttpStatus> {
        return when (API_KEY) {
            priceDTO.key -> {
                LOGGER.info("insertPrice - Received new rate to create: $priceDTO")
                val toSave = Price(priceDTO)
                val saved = priceRepo.save(toSave)

                if (toSave == saved) {
                    LOGGER.info("insertPrice - Rate correctly inserted in the database with id: ${saved.id}")
                    ResponseEntity(HttpStatus.OK)
                } else {
                    LOGGER.warn("insertPrice - Rate NOT inserted")
                    ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
                }
            }
            else -> ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    /**
     * Retrieve all the meter reading from the database
     */
    @RequestMapping(value = ["/meterReadings"], method = [(RequestMethod.GET)])
    internal fun getAllMeterReading(@RequestParam(value = "key", defaultValue = "unknown") key: String): ResponseEntity<List<MeterReadingDTO>> {
        return when (API_KEY) {
            key -> {
                LOGGER.info("getAllMeterReading - Request to retrieve all meter reading")
                val findAll = meterReadingRepo.findAll().map { MeterReadingDTO(key = API_KEY, meterReading = it) }

                LOGGER.info("getAllMeterReading - Returning ${findAll.size} items")
                ResponseEntity(findAll, HttpStatus.OK)
            }
            else -> ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    /**
     * Retrieve all the prices from the database
     */
    @RequestMapping(value = ["/prices"], method = [(RequestMethod.GET)])
    internal fun getAllPrices(@RequestParam(value = "key", defaultValue = "unknown") key: String): ResponseEntity<List<PriceDTO>> {
        return when (API_KEY) {
            key -> {
                LOGGER.info("getAllPrices - Request to retrieve all rates")
                val findAll = priceRepo.findAll().map { PriceDTO(key = API_KEY, price = it) }

                LOGGER.info("getAllPrices - Returning ${findAll.size} items")
                ResponseEntity(findAll, HttpStatus.OK)
            }
            else -> ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    /**
     * Retrieve all the meter reading from the database between two dates
     */
    @RequestMapping(value = ["/meterReadingsBetween"], method = [(RequestMethod.GET)])
    internal fun getAllMeterReadingBetween(@RequestParam(value = "key", defaultValue = "unknown") key: String,
                                           @RequestParam(value = "begin", required = true) begin: Date,
                                           @RequestParam(value = "end", required = true) end: Date): ResponseEntity<List<MeterReading>> {
        return when (API_KEY) {
            key -> {
                LOGGER.info("getAllMeterReadingBetween - Request to retrieve all meter reading")
                val list = meterReadingRepo.findByDateBetween(begin, end)

                LOGGER.info("getAllMeterReadingBetween - Returning ${list.size} items")
                ResponseEntity(list, HttpStatus.OK)
            }
            else -> ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    /**
     * Retrieve all the prices from the database between two dates
     */
    @RequestMapping(value = ["/pricesBetween"], method = [(RequestMethod.GET)])
    internal fun getAllPriceBetween(@RequestParam(value = "key", defaultValue = "unknown") key: String,
                                           @RequestParam(value = "begin", required = true) begin: Date,
                                           @RequestParam(value = "end", required = true) end: Date): ResponseEntity<List<Price>> {
        return when (API_KEY) {
            key -> {
                LOGGER.info("getAllPriceBetween - Request to retrieve all meter rates between $begin and $end")
                val list = priceRepo.findByDateBetween(begin, end)

                LOGGER.info("getAllPriceBetween - Returning ${list.size} items")
                ResponseEntity(list, HttpStatus.OK)
            }
            else -> ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @RequestMapping(value = ["/data"], method = [RequestMethod.GET])
    internal fun getGraphDatas( @RequestParam(value = "key", defaultValue = "unknown") key: String ): ResponseEntity<GraphData>{
        return when(API_KEY){
            key -> {
                LOGGER.info("getGraphDatas - Request to get all graph data")
                val allMeterReading = meterReadingRepo.findAll()
                val allResourcePrice = priceRepo.findAll()

                val graphData = GraphData()

                allMeterReading.forEach {
                    addMeterReadingToGraph(it, allResourcePrice, graphData)
                }

                LOGGER.info("getGraphDatas - Returning values : ${graphData.data.size}")
                ResponseEntity(graphData, HttpStatus.OK)
            }
            else -> ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @RequestMapping(value = ["/dataBetween"], method = [RequestMethod.GET])
    internal fun getGraphDatasBetween( @RequestParam(value = "key", defaultValue = "unknown") key: String,
                                       @RequestParam(value = "begin", required = true) begin: Date,
                                       @RequestParam(value = "end", required = true) end: Date): ResponseEntity<GraphData>{
        return when(API_KEY){
            key -> {
                LOGGER.info("getGraphDatasBetween - Request to get all graph data")
                val allMeterReading = meterReadingRepo.findAll()
                val allResourcePrice = priceRepo.findAll()

                val graphData = GraphData()

                allMeterReading.filter { isBetween(it, end, begin) }.forEach {
                    addMeterReadingToGraph(it, allResourcePrice, graphData)
                }

                LOGGER.info("getGraphDatasBetween - Returning values : ${graphData.data.size}")
                ResponseEntity(graphData, HttpStatus.OK)
            }
            else -> ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @RequestMapping(value = ["/dataForMonth"], method = [RequestMethod.GET])
    internal fun getGraphDatasForMonth( @RequestParam(value = "key", defaultValue = "unknown") key: String,
                                       @RequestParam(value = "month", required = true) month: String): ResponseEntity<GraphData>{
        return when(API_KEY){
            key -> {
                LOGGER.info("getGraphDatasForMonth - Request to get all graph data")
                val allMeterReading = meterReadingRepo.findAll()
                val allResourcePrice = priceRepo.findAll()

                val graphData = GraphData()

                allMeterReading.filter { fromDateToPreviousMonthName(it) == month }.forEach {
                    addMeterReadingToGraph(it, allResourcePrice, graphData)
                }

                LOGGER.info("getGraphDatasForMonth - Returning values : ${graphData.data.size}")
                ResponseEntity(graphData, HttpStatus.OK)
            }
            else -> ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    private fun isBetween(meterReading: MeterReading, end: Date, begin: Date): Boolean {
        val mrLD = LocalDate.from(meterReading.date.toInstant()).minusMonths(1)
        val endLD = LocalDate.from(end.toInstant())
        val beginLD = LocalDate.from(begin.toInstant())

        return mrLD.isBefore(endLD) && mrLD.isAfter(beginLD)
    }


    private fun addMeterReadingToGraph(meterReading: MeterReading, allResourcePrice: MutableList<Price>, graphData: GraphData) {
        val month = fromDateToPreviousMonthName(meterReading)
        val money = allResourcePrice
                .filter { price -> price.resource == meterReading.resource && price.date.before(meterReading.date) }
                .maxBy { price -> price.date }?.number ?: 1L

        graphData.data[meterReading.resource]?.get(month)?.add((money * meterReading.number).toString())
    }

    private fun fromDateToPreviousMonthName(meterReading: MeterReading) =
            LocalDate.from(meterReading.date.toInstant()).minusMonths(1).month.getDisplayName(TextStyle.FULL, Locale.FRANCE)

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger("HouseApi")

        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(HouseRestApi::class.java, *args)
        }
    }
}