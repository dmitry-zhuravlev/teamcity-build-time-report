package com.teamcity.report.converters

import com.teamcity.report.client.dto.Build
import com.teamcity.report.client.dto.Project
import com.teamcity.report.config.TeamCityConfig
import com.teamcity.report.repository.entity.*
import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.JobParametersBuilder
import java.time.ZonedDateTime

/**
 * @author Dmitry Zhuravlev
 *         Date: 22/10/2017
 */

fun Build.toEntity(serverName: String) = BuildEntity(BuildEntityKey(buildType.id, buildType.projectId, finishDate.toMilli(), id, serverName),
        statistics.property.firstOrNull { it.name == "BuildDuration" }?.value?.toLong() ?: 0 //TODO perform additional checks
)

fun Build.toTypeEntity(serverName: String) = BuildTypeEntity(BuildTypeEntityKey(buildType.projectId, buildType.id, serverName), buildType.name)

fun Project.toEntity(serverName: String) = ProjectEntity(ProjectEntityKey(id, parentProjectId, serverName), name)

fun ZonedDateTime.toMilli() = toInstant().toEpochMilli()

fun TeamCityConfig.toJobParameters(): List<JobParameters> = servers.map { serverConfig ->
    JobParametersBuilder()
            .addLong("actualizationDays", serverConfig.worker.actualizationDays)
            .addLong("start", serverConfig.worker.startPage)
            .addLong("chunkSize", serverConfig.worker.chunkSize)
            .addLong("requestTimeoutMs", serverConfig.worker.requestTimeoutMs)
            .addString("serverId", serverConfig.id)
            .addString("serverUrl", serverConfig.url)
            .addString("serverName", serverConfig.name)
            .addString("apiVersion", serverConfig.apiVersion)
            .addString("userName", serverConfig.username)
            .addString("userPassword", serverConfig.password)
            .toJobParameters()
}.toList()