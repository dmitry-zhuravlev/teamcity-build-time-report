package com.teamcity.report.indexer.client.interceptor

import com.teamcity.report.indexer.client.AccessCookieManager
import com.teamcity.report.indexer.properties.TeamCityConfigProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpRequest
import org.springframework.http.HttpStatus
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpResponse
import org.springframework.stereotype.Component

/**
 * @author Dmitry Zhuravlev
 *         Date: 29/10/2017
 */
@Component
class CookieAuthorizationInterceptor(teamCityConfigProperties: TeamCityConfigProperties) : AuthenticationInterceptor(teamCityConfigProperties) {

    @Autowired
    lateinit var accessCookieManager: AccessCookieManager

    override fun doIntercept(serverConfig: TeamCityConfigProperties.ServerConfig, request: HttpRequest, body: ByteArray, execution: ClientHttpRequestExecution): ClientHttpResponse {
        val authorizationCookie = accessCookieManager.resolveAccessCookie(serverConfig.id) ?: return execution.execute(request, body)

        request.headers.add(HttpHeaders.COOKIE, authorizationCookie)

        val response = execution.execute(request, body)
        if (response.statusCode == HttpStatus.UNAUTHORIZED) {
            accessCookieManager.deleteAccessCookie(serverConfig.id, authorizationCookie)
        }
        return response
    }
}