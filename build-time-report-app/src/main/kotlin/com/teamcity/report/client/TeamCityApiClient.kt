package com.teamcity.report.client

import com.teamcity.report.client.dto.Builds
import com.teamcity.report.config.ConfigDefault.WORKER_CHUNK_SIZE
import com.teamcity.report.config.ConfigDefault.WORKER_START_PAGE
import com.teamcity.report.config.TeamCityConfig
import java.time.ZonedDateTime

/**
 * @author Dmitry Zhuravlev
 *         Date:  19.10.2017
 */
interface TeamCityApiClient {
    fun getBuilds(count: Int = WORKER_CHUNK_SIZE, start: Int = WORKER_START_PAGE, serverConfig: TeamCityConfig.ServerConfig, afterDate: ZonedDateTime? = null): Builds
    fun getProjects(count: Int = WORKER_CHUNK_SIZE, start: Int = WORKER_START_PAGE, serverConfig: TeamCityConfig.ServerConfig)
}