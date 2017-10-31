package com.teamcity.report.indexer.client

import com.teamcity.report.indexer.client.model.Builds
import com.teamcity.report.indexer.client.model.Projects
import com.teamcity.report.indexer.config.ConfigDefault.DATE_PATTERN
import com.teamcity.report.indexer.properties.TeamCityConfigProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.http.client.support.BasicAuthorizationInterceptor
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * @author Dmitry Zhuravlev
 *         Date:  19.10.2017
 */
@Scope("prototype")
@Service
class TeamCityApiClientImpl : TeamCityApiClient {

    val dateFormat = DateTimeFormatter.ofPattern(DATE_PATTERN)!!

    @Autowired
    lateinit var accessCookieManager: AccessCookieManager

    private fun buildsRequestUrl(count: Long, start: Long, serverConfig: TeamCityConfigProperties.ServerConfig, afterDate: ZonedDateTime?)
            = UriComponentsBuilder.fromHttpUrl("${serverConfig.url}/httpAuth/app/rest/${serverConfig.apiVersion}/builds?affectedProject:(id:_Root)=&fields=count,nextHref,build(id,number,status,finishDate,buildType(id,name,projectId),statistics(\$locator(name:BuildDuration),property(name,value)))&locator=count:$count,start:$start${afterDateQueryParam(afterDate)}")
            .build(true).toUri()

    private fun projectsRequestUrl(count: Long, start: Long, serverConfig: TeamCityConfigProperties.ServerConfig)
            = UriComponentsBuilder.fromHttpUrl("${serverConfig.url}/httpAuth/app/rest/${serverConfig.apiVersion}/projects?&fields=count,project(id,name,parentProjectId)&locator=count:$count,start:$start")
            .build(true).toUri()

    private fun afterDateQueryParam(afterDate: ZonedDateTime?) = if (afterDate == null) "" else ",finishDate:(date:${dateFormat.format(afterDate).replace("+", "%2B")},condition:after)"

    private fun prepareRestTemplate(serverConfig: TeamCityConfigProperties.ServerConfig) = RestTemplate().apply {
        val accessCookie = accessCookieManager.resolveAccessCookie(serverConfig)
        if (accessCookie != null) {
            interceptors.add(CookieAuthorizationInterceptor(accessCookie))
        } else {
            interceptors.add(BasicAuthorizationInterceptor(serverConfig.username, serverConfig.password))
        }
    }

    override fun getBuilds(count: Long, start: Long, serverConfig: TeamCityConfigProperties.ServerConfig, afterDate: ZonedDateTime?) = with(prepareRestTemplate(serverConfig)) {
        getForEntity(buildsRequestUrl(count, start, serverConfig, afterDate), Builds::class.java).body
    }

    override fun getProjects(count: Long, start: Long, serverConfig: TeamCityConfigProperties.ServerConfig) = with(prepareRestTemplate(serverConfig)) {
        getForEntity(projectsRequestUrl(count, start, serverConfig), Projects::class.java).body
    }
}