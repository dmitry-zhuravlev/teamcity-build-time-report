package com.teamcity.report.client

import com.teamcity.report.client.dto.Builds
import com.teamcity.report.config.TeamCityServerConfig
import org.springframework.http.client.support.BasicAuthorizationInterceptor
import org.springframework.web.client.RestTemplate

/**
 * @author Dmitry Zhuravlev
 *         Date:  19.10.2017
 */
//@Service
class TeamCityApiClientImpl(private val serverConfig: TeamCityServerConfig.Server) : TeamCityApiClient {
    //TODO make configurable
    private fun buildsRequestUrl(count: Int, start: Int) = "${serverConfig.url}/httpAuth/app/rest/builds?affectedProject:(id:_Root)=&fields=count,nextHref,build(id,number,status,finishDate,buildType(id,name,projectName,projectId),statistics(\$locator(name:BuildDuration),property(name,value)))&locator=count:$count,start:$start"

    private fun projectsRequestUrl(count: Int, start: Int) = "${serverConfig.url}/app/rest/projects?&fields=count,project(id,name,parentProjectId)&locator=count:$count,start:$start"

    private var restTemplate = RestTemplate().apply {
        interceptors.add(BasicAuthorizationInterceptor(serverConfig.username, serverConfig.password)) //TODO extract from here
    }

    override fun getBuilds(count: Int, start: Int) = with(restTemplate) {
        getForEntity(buildsRequestUrl(count, start), Builds::class.java).body
    }

    override fun getProjects(count: Int, start: Int) = with(restTemplate) {
        TODO("not implemented")
    }
}