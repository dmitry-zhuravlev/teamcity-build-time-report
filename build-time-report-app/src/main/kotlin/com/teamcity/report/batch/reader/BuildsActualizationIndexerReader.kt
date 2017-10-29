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
import java.time.ZonedDateTime

/**
 * @author Dmitry Zhuravlev
 *         Date:  23.10.2017
 */
@Component
@StepScope
class BuildsActualizationIndexerReader(
        @Value("#{jobParameters['requestTimeoutMs']}")
        private val requestTimeoutMs: Long,

        @Value("#{jobParameters['actualizationDays']}")
        private val actualizationDays: Long,

        @Value("#{jobParameters['start']}")
        private val initialStart: Long,

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

    private val logger = LoggerFactory.getLogger(BuildsActualizationIndexerReader::class.java)

    var currentStart = initialStart

    override fun read(): List<Build>? {
        val serverConfig = TeamCityConfig.ServerConfig(serverId, serverName, apiVersion, serverUrl, userName, userPassword)
        val afterDate = ZonedDateTime.now().minusDays(actualizationDays)
        val builds = client.getBuilds(chunkSize, currentStart, serverConfig, afterDate)
        val buildsList = builds.build
        logger.info("Got the following builds from server '$serverName' $buildsList")
        currentStart += chunkSize
        pauseAfterRead(requestTimeoutMs)
        return if (isLastChunk(builds) && buildsList.isEmpty()) {
            null
        } else {
            buildsList
        }
    }

    private fun pauseAfterRead(requestTimeoutMs: Long) = try {
        Thread.sleep(requestTimeoutMs)
    } catch (e: InterruptedException) {
        logger.warn("Indexer reader sleep interrupted")
    }

    private fun isLastChunk(builds: Builds) = builds.nextHref == null
}