package com.teamcity.report.indexer.client

import com.teamcity.report.indexer.properties.TeamCityConfigProperties.ServerConfig
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.client.ClientHttpResponse
import org.springframework.http.client.support.BasicAuthorizationInterceptor
import org.springframework.stereotype.Service
import org.springframework.web.client.DefaultResponseErrorHandler
import org.springframework.web.client.RestTemplate
import java.util.concurrent.ConcurrentHashMap

/**
 * @author Dmitry Zhuravlev
 *         Date: 29/10/2017
 */
@Service
class AccessCookieManager {

    private val logger = LoggerFactory.getLogger(AccessCookieManager::class.java)

    private val cookiesMap = ConcurrentHashMap<String, String>()

    companion object {
        const val COOKIE_REQUEST_PATH = "/app/rest/server"
    }

    fun resolveAccessCookie(serverConfig: ServerConfig): String? {
        val storedCookie = cookiesMap[serverConfig.id]
        return if (storedCookie != null) checkCookieAndRemoveExpired(storedCookie, serverConfig) else {
            val cookieFromServer = requestCookieFromServer(serverConfig)
            if (cookieFromServer != null) {
                cookiesMap[serverConfig.id] = cookieFromServer
            }
            cookieFromServer
        }
    }

    //TODO probably it is a better idea to check cookie expiration time instead of making request to server but we doesn't know exact expiration time for auth cookie of TeamCity server.
    private fun checkCookieAndRemoveExpired(authorizationCookie: String, serverConfig: ServerConfig): String? = with(RestTemplate()) {
        errorHandler = LoggingResponseErrorHeader()
        interceptors.add(CookieAuthorizationInterceptor(authorizationCookie))
        val statusCode = exchange(serverConfig.url + COOKIE_REQUEST_PATH, HttpMethod.GET, null, String::class.java).statusCode
        return if (statusCode != HttpStatus.UNAUTHORIZED) authorizationCookie else {
            logger.warn("Authorization cookie for server ${serverConfig.url + COOKIE_REQUEST_PATH} expired. Remove it from store.")
            cookiesMap.remove(serverConfig.id, authorizationCookie)
            null
        }
    }

    private fun requestCookieFromServer(serverConfig: ServerConfig): String? = with(RestTemplate()) {
        errorHandler = LoggingResponseErrorHeader()
        val userName = serverConfig.username
        val password = serverConfig.password
        if (userName.isNotBlank()) {
            interceptors.add(BasicAuthorizationInterceptor(userName, password))
        }
        val headers = exchange(serverConfig.url + COOKIE_REQUEST_PATH, HttpMethod.GET, null, String::class.java).headers
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