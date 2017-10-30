package com.teamcity.report.indexer.client

import com.teamcity.report.indexer.batch.reader.BuildsActualizationIndexerReader
import com.teamcity.report.indexer.config.TeamCityConfig.ServerConfig
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.client.support.BasicAuthorizationInterceptor
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.util.concurrent.ConcurrentHashMap

/**
 * @author Dmitry Zhuravlev
 *         Date: 29/10/2017
 */
@Service
class AccessCookieManager {

    private val logger = LoggerFactory.getLogger(BuildsActualizationIndexerReader::class.java)

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
        interceptors.add(CookieAuthorizationInterceptor(authorizationCookie))
        val statusCode = exchange(serverConfig.url + COOKIE_REQUEST_PATH, HttpMethod.GET, null, String::class.java).statusCode
        return if (statusCode != HttpStatus.UNAUTHORIZED) authorizationCookie else {
            logger.warn("Authorization cookie for server ${serverConfig.url + COOKIE_REQUEST_PATH} expired. Remove it from store.")
            cookiesMap.remove(serverConfig.id, authorizationCookie)
            null
        }
    }

    private fun requestCookieFromServer(serverConfig: ServerConfig): String? = with(RestTemplate()) {
        interceptors.add(BasicAuthorizationInterceptor(serverConfig.username, serverConfig.password))
        val headers = exchange(serverConfig.url + COOKIE_REQUEST_PATH, HttpMethod.GET, null, String::class.java).headers
        val cookieHeader = headers.getFirst(HttpHeaders.SET_COOKIE)
        if (cookieHeader == null) {
            logger.warn("Server ${serverConfig.url + COOKIE_REQUEST_PATH} returned no cookie")
        }
        return cookieHeader
    }
}