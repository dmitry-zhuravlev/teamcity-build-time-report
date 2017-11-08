package com.teamcity.report.indexer.client.interceptor

import com.teamcity.report.indexer.properties.TeamCityConfigProperties
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpResponse
import org.springframework.stereotype.Component
import org.springframework.util.Base64Utils
import java.nio.charset.StandardCharsets

/**
 * @author Dmitry Zhuravlev
 *         Date:  08.11.2017
 */
@Component
class BasicAuthorizationInterceptor(teamCityConfigProperties: TeamCityConfigProperties) : AuthenticationInterceptor(teamCityConfigProperties) {

    override fun doIntercept(serverConfig: TeamCityConfigProperties.ServerConfig, request: HttpRequest, body: ByteArray, execution: ClientHttpRequestExecution): ClientHttpResponse {
        request.headers.add("Authorization", "Basic " + buildBase64AuthToken(serverConfig))
        return execution.execute(request, body)
    }

    private fun buildBase64AuthToken(serverConfig: TeamCityConfigProperties.ServerConfig) = Base64Utils.encodeToString(
            (serverConfig.username + ":" + serverConfig.password).toByteArray(StandardCharsets.UTF_8))
}