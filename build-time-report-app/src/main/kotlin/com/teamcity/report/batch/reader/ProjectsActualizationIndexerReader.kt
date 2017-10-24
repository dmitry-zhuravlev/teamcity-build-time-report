package com.teamcity.report.batch.reader

import com.teamcity.report.client.TeamCityApiClient
import com.teamcity.report.client.dto.Project
import com.teamcity.report.config.TeamCityConfig
import org.slf4j.LoggerFactory
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.item.ItemReader
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
 * @author Dmitry Zhuravlev
 *         Date:  24.10.2017
 */
@Component
@StepScope
class ProjectsActualizationIndexerReader(
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
) : ItemReader<List<Project>?> {

    private val logger = LoggerFactory.getLogger(ProjectsActualizationIndexerReader::class.java)

    override fun read(): List<Project>? {
        val serverConfig = TeamCityConfig.ServerConfig(serverId, serverName, apiVersion, serverUrl, userName, userPassword)
        val projects = client.getProjects(chunkSize, start, serverConfig)
        val projectsList = projects.project
        logger.info("Got the following projects from server '$serverName' $projectsList")
        start += chunkSize
        return if (isLastChunk(projectsList)) null else projectsList
    }

    private fun isLastChunk(builds: List<Project>) = builds.isEmpty()
}