package com.teamcity.report.indexer.client

import com.teamcity.report.indexer.client.model.BuildTypes
import com.teamcity.report.indexer.client.model.Builds
import com.teamcity.report.indexer.client.model.Projects
import com.teamcity.report.indexer.converters.Constants.WORKER_CHUNK_SIZE
import com.teamcity.report.indexer.converters.Constants.WORKER_START_PAGE
import com.teamcity.report.indexer.properties.TeamCityConfigProperties
import java.time.ZonedDateTime

/**
 * @author Dmitry Zhuravlev
 *         Date:  19.10.2017
 */
interface TeamCityApiClient {
    fun getBuilds(count: Long = WORKER_CHUNK_SIZE, start: Long = WORKER_START_PAGE, serverConfig: TeamCityConfigProperties.ServerConfig,
                  buildFinishDate: ZonedDateTime? = null, buildFinishDateCondition: String? = null): Builds
    fun getProjects(count: Long = WORKER_CHUNK_SIZE, start: Long = WORKER_START_PAGE, serverConfig: TeamCityConfigProperties.ServerConfig): Projects
    fun getBuildTypes(count: Long = WORKER_CHUNK_SIZE, start: Long = WORKER_START_PAGE, serverConfig: TeamCityConfigProperties.ServerConfig): BuildTypes
}