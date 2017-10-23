package com.teamcity.report.batch.reader

import com.teamcity.report.client.TeamCityApiClient
import com.teamcity.report.client.dto.Build
import com.teamcity.report.config.TeamCityConfig
import org.slf4j.LoggerFactory
import org.springframework.batch.item.ItemReader
import java.time.ZonedDateTime

/**
 * @author Dmitry Zhuravlev
 *         Date:  23.10.2017
 */
class BuildsActualizationIndexerReader(private val client: TeamCityApiClient, private val serverConfig: TeamCityConfig.ServerConfig) : ItemReader<List<Build>?> {

    private val logger = LoggerFactory.getLogger(BuildsActualizationIndexerReader::class.java)
    private var start = serverConfig.worker.startPage

    private val afterDate = ZonedDateTime.now().minusDays(serverConfig.worker.actualizationDays)

    override fun read() = with(serverConfig.worker) {
        val builds = client.getBuilds(chunkSize, start, serverConfig, afterDate)
        val buildsList = builds.build
        logger.info("Got following builds from server '${serverConfig.name}' $buildsList")
        start += chunkSize
        if (isLastChunk(buildsList)) null else buildsList
    }

    private fun isLastChunk(builds: List<Build>) = builds.isEmpty()
}