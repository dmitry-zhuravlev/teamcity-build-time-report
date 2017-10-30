package com.teamcity.report.indexer.batch.reader

import com.teamcity.report.indexer.client.TeamCityApiClient
import com.teamcity.report.indexer.client.model.Build
import org.springframework.batch.core.configuration.annotation.StepScope
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
        private val initialStart: Long,

        @Value("#{jobParameters['requestTimeoutMs']}")
        private val requestTimeoutMs: Long,

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

) : AbstractIndexerReader<Build>(requestTimeoutMs, chunkSize, serverName,
        serverId, serverUrl, initialStart, apiVersion, userName, userPassword) {

        override fun executeRequest(currentStart: Long) = client.getBuilds(chunkSize, currentStart, serverConfig)
}