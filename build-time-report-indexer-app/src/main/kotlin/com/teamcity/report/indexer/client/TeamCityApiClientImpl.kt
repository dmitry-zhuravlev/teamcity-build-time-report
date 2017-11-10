package com.teamcity.report.indexer.client

import com.teamcity.report.indexer.client.interceptor.CookieAuthorizationInterceptor
import com.teamcity.report.indexer.client.model.Builds
import com.teamcity.report.indexer.client.model.Projects
import com.teamcity.report.indexer.converters.Constants.DATE_PATTERN
import com.teamcity.report.indexer.properties.TeamCityConfigProperties
import com.teamcity.report.indexer.properties.isGuestAccess
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Service
import org.springframework.web.client.RestOperations
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.annotation.PostConstruct

/**
 * @author Dmitry Zhuravlev
 *         Date:  19.10.2017
 */
@Scope("prototype")
@Service
class TeamCityApiClientImpl : TeamCityApiClient {

    val dateFormat = DateTimeFormatter.ofPattern(DATE_PATTERN)!!

    lateinit var restTemplate: RestOperations

    @Autowired
    lateinit var authorizationInterceptor: CookieAuthorizationInterceptor

    @PostConstruct
    fun init() {
        restTemplate = RestTemplate().apply {
            interceptors.add(authorizationInterceptor)
        }
    }

    private fun buildsRequestUrl(count: Long, start: Long, serverConfig: TeamCityConfigProperties.ServerConfig, afterDate: ZonedDateTime?, condition: String?)
            = UriComponentsBuilder.fromHttpUrl("${serverConfig.url}/${if (serverConfig.isGuestAccess()) "guestAuth" else "httpAuth"}/app/rest/${serverConfig.apiVersion}/builds?affectedProject:(id:_Root)=&fields=count,nextHref,build(id,number,status,finishDate,buildType(id,name,projectId),statistics(\$locator(name:BuildDuration),property(name,value)))&locator=count:$count,start:$start${finishDateQueryParam(afterDate, condition)}")
            .build(true).toUri()

    private fun projectsRequestUrl(count: Long, start: Long, serverConfig: TeamCityConfigProperties.ServerConfig)
            = UriComponentsBuilder.fromHttpUrl("${serverConfig.url}/${if (serverConfig.isGuestAccess()) "guestAuth" else "httpAuth"}/app/rest/${serverConfig.apiVersion}/projects?&fields=count,project(id,name,parentProjectId)&locator=count:$count,start:$start")
            .build(true).toUri()

    private fun finishDateQueryParam(finishDate: ZonedDateTime?, condition: String?) = if (finishDate == null) "" else ",finishDate:(date:${dateFormat.format(finishDate).replace("+", "%2B")},condition:$condition)"

    override fun getBuilds(count: Long, start: Long, serverConfig: TeamCityConfigProperties.ServerConfig, buildFinishDate: ZonedDateTime?, buildFinishDateCondition: String?) = with(restTemplate) {
        getForEntity(buildsRequestUrl(count, start, serverConfig, buildFinishDate, buildFinishDateCondition), Builds::class.java).body
    }

    override fun getProjects(count: Long, start: Long, serverConfig: TeamCityConfigProperties.ServerConfig) = with(restTemplate) {
        getForEntity(projectsRequestUrl(count, start, serverConfig), Projects::class.java).body
    }
}