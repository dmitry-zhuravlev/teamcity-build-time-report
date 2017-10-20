package com.teamcity.report.config

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
data class TeamCityServerConfig(var servers: List<Server> = mutableListOf()) {
    data class Server(var name: String = "", var apiVersion: String = "", var url: String = "",
                      var username: String = "", var password: String = "")
}

@Component
@ConfigurationProperties("worker")
data class WorkerConfig(val requestTimeoutMs: Int = WORKER_REQUEST_TIMEOUT_MS, val chunkSize: Int = WORKER_CHUNK_SIZE, val startPage: Int = WORKER_START_PAGE)