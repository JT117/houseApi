package io.swtf.jt.security

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import java.security.MessageDigest
import java.time.Duration
import java.time.Instant
import java.util.*
import javax.xml.bind.DatatypeConverter

data class AuthorizationData(val hash: String, val insertedAt: Instant)

@Service
open class AuthorizationManager {

    @Value("\${username}")
    private lateinit var username: String

    @Value("\${password}")
    private lateinit var password: String

    private val map: MutableMap<String, AuthorizationData> = mutableMapOf()

    fun login(user: String, pass: String, ip: String, userAgent: String): String? {
        return if (user == username && pass == password) {
            cleanUp()
            val uuid = UUID.randomUUID().toString()
            addData(uuid, userAgent, ip)
            uuid
        } else {
            null
        }
    }

    private fun addData(token: String, userAgent: String, ip: String) {
        if (!StringUtils.isEmpty(userAgent) && !StringUtils.isEmpty(ip)) {
            map[token] = AuthorizationData(computeHash(userAgent, ip), Instant.now())
        } else {
            LOGGER.warn("Trying to add authorization data with empty value: $userAgent, $ip")
        }
    }

    private fun searchToken(token: String): AuthorizationData? {
        return if (map.containsKey(token)) {
            LOGGER.info("Match found for the given token")
            map[token]
        } else {
            LOGGER.warn("NO Match found for the given token")
            null
        }
    }

    fun isValid(token: String, ip: String, userAgent: String): Boolean {
        val data = searchToken(token)
        if (data != null) {
            return data.hash == computeHash(userAgent, ip)
        }
        return false
    }

    private fun cleanUp() {
        val tokenToRemove = map.filter { Duration.between(it.value.insertedAt, Instant.now()).seconds > 3600 }.map { it.key }
        tokenToRemove.forEach { map.remove(it) }
    }

    private fun computeHash(userAgent: String, ip: String) = DatatypeConverter.printHexBinary(messageDigest.digest("$userAgent-$ip".toByteArray())).toUpperCase()

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger("AuthorizationMap")
        val messageDigest: MessageDigest = MessageDigest.getInstance("SHA-512")
    }
}