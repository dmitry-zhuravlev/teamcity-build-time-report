package com.teamcity.report.client

import com.teamcity.report.client.dto.Builds
import com.teamcity.report.config.ConfigDefault.DATE_PATTERN
import com.teamcity.report.config.TeamCityConfig
import org.springframework.http.client.support.BasicAuthorizationInterceptor
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * @author Dmitry Zhuravlev
 *         Date:  19.10.2017
 */
@Service
class TeamCityApiClientImpl : TeamCityApiClient {

    val dateFormat = DateTimeFormatter.ofPattern(DATE_PATTERN)

    //TODO make configurable
    private fun buildsRequestUrl(count: Int, start: Int, serverConfig: TeamCityConfig.ServerConfig, finishDate: ZonedDateTime?)
            = "${serverConfig.url}/httpAuth/app/rest/builds?affectedProject:(id:_Root)=&fields=count,nextHref,build(id,number,status,finishDate,buildType(id,name,projectName,projectId),statistics(\$locator(name:BuildDuration),property(name,value)))&locator=count:$count,start:$start${afterDatePathParam(finishDate)}"

    private fun afterDatePathParam(afterDate: ZonedDateTime?) = if (afterDate == null) "" else ",finishDate:(date:${dateFormat.format(afterDate)},condition:after)"

    private fun projectsRequestUrl(count: Int, start: Int, serverConfig: TeamCityConfig.ServerConfig)
            = "${serverConfig.url}/app/rest/projects?&fields=count,project(id,name,parentProjectId)&locator=count:$count,start:$start"

    private fun restTemplate(serverConfig: TeamCityConfig.ServerConfig) = RestTemplate().apply {
        interceptors.add(BasicAuthorizationInterceptor(serverConfig.username, serverConfig.password)) //TODO extract from here
    }

    override fun getBuilds(count: Int, start: Int, serverConfig: TeamCityConfig.ServerConfig, afterDate: ZonedDateTime?) = with(restTemplate(serverConfig)) {
        getForEntity(buildsRequestUrl(count, start, serverConfig, afterDate), Builds::class.java).body
    }

    override fun getProjects(count: Int, start: Int, serverConfig: TeamCityConfig.ServerConfig) = with(restTemplate(serverConfig)) {
        TODO("not implemented")
    }
}