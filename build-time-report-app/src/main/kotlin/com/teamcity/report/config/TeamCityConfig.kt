package com.teamcity.report.config

import com.teamcity.report.config.ConfigDefault.WORKER_ACTUALIZATION_DAYS
import com.teamcity.report.config.ConfigDefault.WORKER_CHUNK_SIZE
import com.teamcity.report.config.ConfigDefault.WORKER_REQUEST_TIMEOUT_MS
import com.teamcity.report.config.ConfigDefault.WORKER_START_PAGE
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * @author Dmitry Zhuravlev
 *         Date:  19.10.2017
 */
@Component
@ConfigurationProperties("teamcity")
data class TeamCityConfig(var servers: List<ServerConfig> = mutableListOf()) {
    data class ServerConfig(var name: String = "", var apiVersion: String = "", var url: String = "",
                            var username: String = "", var password: String = "", var worker: WorkerConfig = WorkerConfig()) {
        @Component
        @ConfigurationProperties("worker")
        data class WorkerConfig(var requestTimeoutMs: Long = WORKER_REQUEST_TIMEOUT_MS,
                                var chunkSize: Int = WORKER_CHUNK_SIZE,
                                var startPage: Int = WORKER_START_PAGE,
                                var actualizationDays: Int = WORKER_ACTUALIZATION_DAYS)
    }
}
