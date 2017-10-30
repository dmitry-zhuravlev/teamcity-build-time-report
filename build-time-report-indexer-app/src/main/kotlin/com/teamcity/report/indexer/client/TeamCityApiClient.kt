package com.teamcity.report.indexer.client

import com.teamcity.report.indexer.client.dto.Builds
import com.teamcity.report.indexer.client.dto.Projects
import com.teamcity.report.indexer.config.ConfigDefault.WORKER_CHUNK_SIZE
import com.teamcity.report.indexer.config.ConfigDefault.WORKER_START_PAGE
import com.teamcity.report.indexer.config.TeamCityConfig
import java.time.ZonedDateTime

/**
 * @author Dmitry Zhuravlev
 *         Date:  19.10.2017
 */
interface TeamCityApiClient {
    fun getBuilds(count: Long = WORKER_CHUNK_SIZE, start: Long = WORKER_START_PAGE, serverConfig: TeamCityConfig.ServerConfig, afterDate: ZonedDateTime? = null): Builds
    fun getProjects(count: Long = WORKER_CHUNK_SIZE, start: Long = WORKER_START_PAGE, serverConfig: TeamCityConfig.ServerConfig): Projects
}