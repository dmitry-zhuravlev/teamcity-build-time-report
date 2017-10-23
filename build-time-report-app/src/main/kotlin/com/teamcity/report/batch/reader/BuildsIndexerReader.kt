package com.teamcity.report.batch.reader

import com.teamcity.report.client.TeamCityApiClient
import com.teamcity.report.client.dto.Build
import com.teamcity.report.client.dto.Builds
import com.teamcity.report.config.TeamCityConfig
import org.slf4j.LoggerFactory
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.item.ItemReader
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component


/**
 * @author Dmitry Zhuravlev
 *         Date:  19.10.2017
 */
@Component
@StepScope
class BuildsIndexerReader(
        @Value("#{jobParameters['start']}")
        private var start: Long,

        @Value("#{jobParameters['chunkSize']}")
        private val chunkSize: Long,

        @Value("#{jobParameters['serverId']}")
        private val serverId: String,

        @Value("#{jobParameters['serverName']}")
        private val serverName: String,

        @Value("#{jobParameters['serverUrl']}")
        private val serverUrl: String,

        @Value("#{jobParameters['apiVersion']}")
        private val apiVersion: String,

        @Value("#{jobParameters['userName']}")
        private val userName: String,

        @Value("#{jobParameters['userPassword']}")
        private val userPassword: String,

        @Autowired
        private var client: TeamCityApiClient
) : ItemReader<List<Build>?> {

    private val logger = LoggerFactory.getLogger(BuildsIndexerReader::class.java)

    override fun read(): List<Build>? {
        val serverConfig = TeamCityConfig.ServerConfig(serverId, serverName, apiVersion, serverUrl, userName, userPassword)
        val builds = client.getBuilds(chunkSize, start, serverConfig)
        val buildsList = builds.build
        logger.info("Got following builds from server '$serverName' $buildsList")
        start += chunkSize
        return if (isLastChunk(buildsList)) null else buildsList
    }

    private fun isLastChunk(builds: List<Build>) = builds.isEmpty()
    private fun isLastChunk(builds: Builds) = builds.nextHref == null
}