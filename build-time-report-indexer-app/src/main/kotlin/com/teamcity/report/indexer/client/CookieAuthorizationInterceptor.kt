package com.teamcity.report.indexer.client

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.util.Assert

/**
 * @author Dmitry Zhuravlev
 *         Date: 29/10/2017
 */
class CookieAuthorizationInterceptor(private val authorizationCookie: String) : ClientHttpRequestInterceptor {
    init {
        Assert.hasLength(authorizationCookie, "authorization cookie should be not empty")
    }

    override fun intercept(request: HttpRequest, body: ByteArray, execution: ClientHttpRequestExecution): ClientHttpResponse {
        request.headers.add(HttpHeaders.COOKIE, authorizationCookie)
        return execution.execute(request, body)
    }
}