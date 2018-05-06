package io.swtf.jt

import io.swtf.jt.dao.MeterReading
import io.swtf.jt.dao.MeterReadingRepo
import io.swtf.jt.dao.Price
import io.swtf.jt.dao.PriceRepo
import io.swtf.jt.dto.*
import io.swtf.jt.security.AuthorizationManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import javax.servlet.http.HttpServletRequest

@RestController
@EnableAutoConfiguration
@SpringBootApplication
open class HouseRestApi {

    @Autowired
    private lateinit var meterReadingRepo: MeterReadingRepo

    @Autowired
    private lateinit var priceRepo: PriceRepo

    @Autowired
    private lateinit var authorizationManager: AuthorizationManager

    /**
     * Authenticate a user with an username and password, return a mandatory UUID to call the other method
     */
    @RequestMapping(value = ["/authenticate"], method = [(RequestMethod.POST)])
    internal fun authenticate(@RequestHeader(value = "User-Agent") userAgent: String,
                              @RequestBody authorizationDTO: AuthorizationDTO,
                              request: HttpServletRequest): ResponseEntity<String> {
        val hash = authorizationManager.login(authorizationDTO.username, authorizationDTO.password, request.remoteAddr, userAgent)

        return if (hash != null) {
            ResponseEntity(hash, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    /**
     * Insert a new meter reading in the database
     */
    @RequestMapping(value = ["/meterReading"], method = [(RequestMethod.POST)])
    internal fun insertMeterReading(@RequestHeader(value = "Authorization") authorization: String,
                                    @RequestHeader(value = "User-Agent") userAgent: String,
                                    @RequestBody meterReadingDTO: MeterReadingDTO,
                                    request: HttpServletRequest): ResponseEntity<MeterReading> {
        return when {
            authorizationManager.isValid(authorization, request.remoteAddr, userAgent) -> {
                LOGGER.info("insertMeterReading - Received meter reading to create: $meterReadingDTO")

                if (meterReadingDTO.isCumulative) {
                    val lastMeterReadingNumber = meterReadingRepo.findAllByResource(meterReadingDTO.resource).maxBy { it.date }?.number
                            ?: 0L
                    meterReadingDTO.number -= lastMeterReadingNumber
                }

                val toSave = MeterReading(meterReadingDTO)
                val meterReading = meterReadingRepo.save(toSave)

                if (toSave == meterReading) {
                    LOGGER.info("insertMeterReading - Meter reading correctly inserted in the database with id: ${meterReading.id}")
                    ResponseEntity(HttpStatus.OK)
                } else {
                    LOGGER.warn("insertMeterReading - Meter reading NOT inserted")
                    ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
                }
            }
            authorization.isNotEmpty() -> ResponseEntity(HttpStatus.UNAUTHORIZED)
            else -> ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    /**
     * Insert a new price in the database
     */
    @RequestMapping(value = ["/price"], method = [(RequestMethod.POST)])
    internal fun insertPrice(@RequestHeader(value = "Authorization") authorization: String,
                             @RequestHeader(value = "User-Agent") userAgent: String,
                             @RequestBody priceDTO: PriceDTO,
                             request: HttpServletRequest): ResponseEntity<HttpStatus> {
        return when {
            authorizationManager.isValid(authorization, request.remoteAddr, userAgent) -> {
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
            authorization.isNotEmpty() -> ResponseEntity(HttpStatus.UNAUTHORIZED)
            else -> ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    /**
     * Retrieve all the meter reading from the database
     */
    @RequestMapping(value = ["/meterReadings"], method = [(RequestMethod.GET)])
    internal fun getAllMeterReading(@RequestHeader(value = "Authorization") authorization: String,
                                    @RequestHeader(value = "User-Agent") userAgent: String,
                                    request: HttpServletRequest): ResponseEntity<List<MeterReadingOutDTO>> {
        return when {
            authorizationManager.isValid(authorization, request.remoteAddr, userAgent) -> {
                LOGGER.info("getAllMeterReading - Request to retrieve all meter reading")
                val findAll = meterReadingRepo.findAll().map { MeterReadingOutDTO(meterReading = it) }

                LOGGER.info("getAllMeterReading - Returning ${findAll.size} items")
                ResponseEntity(findAll, HttpStatus.OK)
            }
            authorization.isNotEmpty() -> ResponseEntity(HttpStatus.UNAUTHORIZED)
            else -> ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    /**
     * Retrieve all the prices from the database
     */
    @RequestMapping(value = ["/prices"], method = [(RequestMethod.GET)])
    internal fun getAllPrices(@RequestHeader(value = "Authorization") authorization: String,
                              @RequestHeader(value = "User-Agent") userAgent: String,
                              request: HttpServletRequest): ResponseEntity<List<PriceDTO>> {
        return when {
            authorizationManager.isValid(authorization, request.remoteAddr, userAgent) -> {
                LOGGER.info("getAllPrices - Request to retrieve all rates")
                val findAll = priceRepo.findAll().map { PriceDTO(price = it) }

                LOGGER.info("getAllPrices - Returning ${findAll.size} items")
                ResponseEntity(findAll, HttpStatus.OK)
            }
            authorization.isNotEmpty() -> ResponseEntity(HttpStatus.UNAUTHORIZED)
            else -> ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    /**
     * Retrieve all the meter reading from the database between two dates
     */
    @RequestMapping(value = ["/meterReadingsBetween"], method = [(RequestMethod.GET)])
    internal fun getAllMeterReadingBetween(@RequestHeader(value = "Authorization") authorization: String,
                                           @RequestHeader(value = "User-Agent") userAgent: String,
                                           @RequestParam(value = "begin", required = true) begin: Date,
                                           @RequestParam(value = "end", required = true) end: Date,
                                           request: HttpServletRequest): ResponseEntity<List<MeterReading>> {
        return when {
            authorizationManager.isValid(authorization, request.remoteAddr, userAgent) -> {
                LOGGER.info("getAllMeterReadingBetween - Request to retrieve all meter reading")
                val list = meterReadingRepo.findByDateBetween(begin, end)

                LOGGER.info("getAllMeterReadingBetween - Returning ${list.size} items")
                ResponseEntity(list, HttpStatus.OK)
            }
            authorization.isNotEmpty() -> ResponseEntity(HttpStatus.UNAUTHORIZED)
            else -> ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    /**
     * Retrieve all the prices from the database between two dates
     */
    @RequestMapping(value = ["/pricesBetween"], method = [(RequestMethod.GET)])
    internal fun getAllPriceBetween(@RequestHeader(value = "Authorization") authorization: String,
                                    @RequestHeader(value = "User-Agent") userAgent: String,
                                    @RequestParam(value = "begin", required = true) begin: Date,
                                    @RequestParam(value = "end", required = true) end: Date,
                                    request: HttpServletRequest): ResponseEntity<List<Price>> {
        return when {
            authorizationManager.isValid(authorization, request.remoteAddr, userAgent) -> {
                LOGGER.info("getAllPriceBetween - Request to retrieve all meter rates between $begin and $end")
                val list = priceRepo.findByDateBetween(begin, end)

                LOGGER.info("getAllPriceBetween - Returning ${list.size} items")
                ResponseEntity(list, HttpStatus.OK)
            }
            authorization.isNotEmpty() -> ResponseEntity(HttpStatus.UNAUTHORIZED)
            else -> ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @RequestMapping(value = ["/data"], method = [RequestMethod.GET])
    internal fun getGraphDatas(@RequestHeader(value = "Authorization") authorization: String,
                               @RequestHeader(value = "User-Agent") userAgent: String,
                               request: HttpServletRequest): ResponseEntity<GraphData> {
        return when {
            authorizationManager.isValid(authorization, request.remoteAddr, userAgent) -> {
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
            authorization.isNotEmpty() -> ResponseEntity(HttpStatus.UNAUTHORIZED)
            else -> ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @RequestMapping(value = ["/dataBetween"], method = [RequestMethod.GET])
    internal fun getGraphDatasBetween(@RequestHeader(value = "Authorization") authorization: String,
                                      @RequestHeader(value = "User-Agent") userAgent: String,
                                      @RequestParam(value = "begin", required = true) begin: Date,
                                      @RequestParam(value = "end", required = true) end: Date,
                                      request: HttpServletRequest): ResponseEntity<GraphData> {
        return when {
            authorizationManager.isValid(authorization, request.remoteAddr, userAgent) -> {
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
            authorization.isNotEmpty() -> ResponseEntity(HttpStatus.UNAUTHORIZED)
            else -> ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @RequestMapping(value = ["/dataForMonth"], method = [RequestMethod.GET])
    internal fun getGraphDatasForMonth(@RequestHeader(value = "Authorization") authorization: String,
                                       @RequestHeader(value = "User-Agent") userAgent: String,
                                       @RequestParam(value = "month", required = true) month: String,
                                       request: HttpServletRequest): ResponseEntity<GraphData> {
        return when {
            authorizationManager.isValid(authorization, request.remoteAddr, userAgent) -> {
                LOGGER.info("getGraphDatasForMonth - Request to get all graph data")
                val allMeterReading = meterReadingRepo.findAll()
                val allResourcePrice = priceRepo.findAll()

                val graphData = GraphData()

                allMeterReading.filter { fromDateToPreviousMonthYear(it) == month }.forEach {
                    addMeterReadingToGraph(it, allResourcePrice, graphData)
                }

                LOGGER.info("getGraphDatasForMonth - Returning values : ${graphData.data.size}")
                ResponseEntity(graphData, HttpStatus.OK)
            }
            authorization.isNotEmpty() -> ResponseEntity(HttpStatus.UNAUTHORIZED)
            else -> ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    private fun isBetween(meterReading: MeterReading, end: Date, begin: Date): Boolean {
        val mrLD = LocalDateTime.ofInstant(meterReading.date.toInstant(), ZoneId.systemDefault()).minusMonths(1)
        val endLD = LocalDateTime.ofInstant(end.toInstant(), ZoneId.systemDefault())
        val beginLD = LocalDateTime.ofInstant(begin.toInstant(), ZoneId.systemDefault())

        return mrLD.isBefore(endLD) && mrLD.isAfter(beginLD)
    }

    private fun addMeterReadingToGraph(meterReading: MeterReading, allResourcePrice: MutableList<Price>, graphData: GraphData) {
        val month = fromDateToPreviousMonthYear(meterReading)
        val money = allResourcePrice
                .filter { price -> price.resource == meterReading.resource && price.date.before(meterReading.date) }
                .maxBy { price -> price.date }?.number ?: 1L

        val mapForResource = graphData.data[meterReading.resource]

        if (mapForResource?.get(month) != null) {
            if (mapForResource[month]?.size == 2) {
                val previousValue = mapForResource[month]?.get(0)?.toLong() ?: 0L
                val previousPrice = mapForResource[month]?.get(1)?.toLong() ?: 0L
                mapForResource[month]?.clear()
                mapForResource[month]?.add((meterReading.number + previousValue).toString())
                mapForResource[month]?.add((money * meterReading.number + previousPrice).toString())
            } else {
                mapForResource[month]?.add(meterReading.number.toString())
                mapForResource[month]?.add((money * meterReading.number).toString())
            }
        } else {
            mapForResource?.put(month, mutableListOf(meterReading.number.toString(), (money * meterReading.number).toString()))
        }
    }

    private fun fromDateToPreviousMonthYear(meterReading: MeterReading): String {
        val minusMonths = LocalDateTime.ofInstant(meterReading.date.toInstant(), ZoneId.systemDefault())
        return datePattern.format(minusMonths)
    }

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger("HouseApi")
        val datePattern: DateTimeFormatter = DateTimeFormatter.ofPattern("MM-yyyy")

        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(HouseRestApi::class.java, *args)
        }
    }
}