package com.teamcity.report.indexer.client

import com.teamcity.report.indexer.client.interceptor.BasicAuthorizationInterceptor
import com.teamcity.report.indexer.properties.TeamCityConfigProperties
import com.teamcity.report.indexer.properties.TeamCityConfigProperties.ServerConfig
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.client.ClientHttpResponse
import org.springframework.stereotype.Service
import org.springframework.web.client.DefaultResponseErrorHandler
import org.springframework.web.client.RestOperations
import org.springframework.web.client.RestTemplate
import java.util.concurrent.ConcurrentHashMap
import javax.annotation.PostConstruct

/**
 * @author Dmitry Zhuravlev
 *         Date: 29/10/2017
 */
@Service
class AccessCookieManager(teamCityConfigProperties: TeamCityConfigProperties) {

    companion object {
        private val logger = LoggerFactory.getLogger(AccessCookieManager::class.java)

        const val COOKIE_REQUEST_PATH = "/app/rest/server"
    }

    private val cookiesMap = ConcurrentHashMap<String, String>()

    private val idToServerConfigMap = teamCityConfigProperties.servers.map { server -> server.id to server }.toMap()

    @Autowired
    lateinit var authorizationInterceptor: BasicAuthorizationInterceptor

    lateinit var restTemplate: RestOperations

    @PostConstruct
    fun init() {
        restTemplate = RestTemplate().apply {
            errorHandler = LoggingResponseErrorHeader()
            interceptors.add(authorizationInterceptor)
        }
    }

    fun deleteAccessCookie(serverConfigId: String, oldAccessCookie: String) {
        cookiesMap.remove(serverConfigId, oldAccessCookie)
    }

    fun resolveAccessCookie(serverConfigId: String): String? {
        val serverConfig: ServerConfig = idToServerConfigMap[serverConfigId] ?: return null
        val storedCookie = cookiesMap[serverConfig.id]
        if (storedCookie != null) {
            return storedCookie
        }
        synchronized(serverConfig) {
            val currentCookie = cookiesMap[serverConfig.id]
            if (currentCookie != null) {
                return currentCookie
            }
            logger.info("Cannot find cached auth cookie for server ${serverConfig.url + COOKIE_REQUEST_PATH}. Will be requested from server.")
            val newAccessCookie = requestCookieFromServer(serverConfig)
            if (newAccessCookie != null) {
                cookiesMap[serverConfig.id] = newAccessCookie
            }
            return newAccessCookie
        }
    }

    private fun requestCookieFromServer(serverConfig: ServerConfig): String? = with(restTemplate) {
        val response = exchange(serverConfig.url + COOKIE_REQUEST_PATH, HttpMethod.GET, null, String::class.java)
        if (response.statusCode == HttpStatus.UNAUTHORIZED) return null
        val headers = response.headers
        val cookieHeader = headers.getFirst(HttpHeaders.SET_COOKIE)
        if (cookieHeader == null) {
            logger.warn("Server ${serverConfig.url + COOKIE_REQUEST_PATH} returned no cookie")
        }
        cookieHeader
    }

    private inner class LoggingResponseErrorHeader : DefaultResponseErrorHandler() {
        override fun handleError(response: ClientHttpResponse) {
            logger.warn("Got error status from server: ${response.statusCode.value()}")
        }
    }
}