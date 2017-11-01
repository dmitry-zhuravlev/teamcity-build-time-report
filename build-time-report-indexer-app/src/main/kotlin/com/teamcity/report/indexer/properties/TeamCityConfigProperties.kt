package com.teamcity.report.indexer.properties

import com.teamcity.report.indexer.converters.Constants.WORKER_ACTUALIZATION_DAYS
import com.teamcity.report.indexer.converters.Constants.WORKER_CHUNK_SIZE
import com.teamcity.report.indexer.converters.Constants.WORKER_REQUEST_TIMEOUT_MS
import com.teamcity.report.indexer.converters.Constants.WORKER_START_PAGE
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * @author Dmitry Zhuravlev
 *         Date:  19.10.2017
 */
@Component
@ConfigurationProperties("teamcity")
data class TeamCityConfigProperties(var servers: List<ServerConfig> = mutableListOf()) {
    data class ServerConfig(var id: String = "", var name: String = "", var apiVersion: String = "", var url: String = "",
                            var username: String = "", var password: String = "", var worker: WorkerConfig = WorkerConfig()) {
        @Component
        @ConfigurationProperties("worker")
        data class WorkerConfig(var requestTimeoutMs: Long = WORKER_REQUEST_TIMEOUT_MS,
                                var chunkSize: Long = WORKER_CHUNK_SIZE,
                                var startPage: Long = WORKER_START_PAGE,
                                var actualizationDays: Long = WORKER_ACTUALIZATION_DAYS)
    }
}
