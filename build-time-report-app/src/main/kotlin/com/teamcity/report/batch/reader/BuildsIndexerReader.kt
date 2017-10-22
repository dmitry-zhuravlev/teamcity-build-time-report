package com.teamcity.report.batch.reader

import com.teamcity.report.client.TeamCityApiClient
import com.teamcity.report.client.dto.Build
import com.teamcity.report.client.dto.Builds
import com.teamcity.report.config.TeamCityConfig
import org.slf4j.LoggerFactory
import org.springframework.batch.item.ItemReader


/**
 * @author Dmitry Zhuravlev
 *         Date:  19.10.2017
 */
//@Component
//@StepScope
class BuildsIndexerReader(private val client: TeamCityApiClient, private val serverConfig: TeamCityConfig.ServerConfig) : ItemReader<List<Build>?> {


    val logger = LoggerFactory.getLogger(BuildsIndexerReader::class.java)

    var start = serverConfig.worker.startPage
    override fun read() = with(serverConfig.worker) {
        val builds = client.getBuilds(chunkSize, start, serverConfig)
        val buildsList = builds.build
        logger.info("Got following builds from server '${serverConfig.name}' $buildsList")
        start += chunkSize
        if (isLastChunk(buildsList)) null else buildsList
    }

    private fun isLastChunk(builds: List<Build>) = builds.isEmpty()
    private fun isLastChunk(builds: Builds) = builds.nextHref == null
}