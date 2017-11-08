package com.teamcity.report.indexer.client.interceptor

import com.teamcity.report.indexer.properties.TeamCityConfigProperties
import com.teamcity.report.indexer.properties.isGuestAccess
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse

/**
 * @author Dmitry Zhuravlev
 *         Date:  08.11.2017
 */
abstract class AuthenticationInterceptor(teamCityConfigProperties: TeamCityConfigProperties) : ClientHttpRequestInterceptor {
    private val urlToConfigMap: Map<String, TeamCityConfigProperties.ServerConfig> =
            teamCityConfigProperties.servers.map { server -> server.url to server }.toMap()

    abstract fun doIntercept(serverConfig: TeamCityConfigProperties.ServerConfig, request: HttpRequest, body: ByteArray, execution: ClientHttpRequestExecution): ClientHttpResponse

    final override fun intercept(request: HttpRequest, body: ByteArray, execution: ClientHttpRequestExecution): ClientHttpResponse {
        val serverConfig = urlToConfigMap["${request.uri.scheme}://${request.uri.authority}"]
        if (serverConfig != null && !serverConfig.isGuestAccess()) {
            return doIntercept(serverConfig, request, body, execution)
        }
        return execution.execute(request, body)
    }
}