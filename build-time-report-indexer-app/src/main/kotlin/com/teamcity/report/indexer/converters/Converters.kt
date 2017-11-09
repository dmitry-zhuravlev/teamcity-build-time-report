package com.teamcity.report.indexer.converters

import com.teamcity.report.indexer.client.model.Build
import com.teamcity.report.indexer.client.model.Project
import com.teamcity.report.indexer.properties.TeamCityConfigProperties
import com.teamcity.report.repository.entity.*
import org.springframework.batch.core.JobParametersBuilder
import java.time.ZonedDateTime

/**
 * @author Dmitry Zhuravlev
 *         Date: 22/10/2017
 */

fun Build.toEntity(serverName: String) = BuildEntity(BuildEntityKey(buildType.id, finishDate.toMilli(), id, serverName),
        statistics.property.firstOrNull { it.name == "BuildDuration" }?.value?.toLongSafe() ?: 0, buildType.projectId)

fun Build.toTypeEntity(serverName: String) = BuildTypeEntity(BuildTypeEntityKey(serverName, buildType.id), buildType.name, buildType.projectId)

fun Project.toEntity(serverName: String) = ProjectEntity(ProjectEntityKey(serverName, id), name, parentProjectId)

fun ZonedDateTime.toMilli() = toInstant().toEpochMilli()

fun String?.toLongSafe(): Long? = try {
    this?.toLong()
} catch (e: NumberFormatException) {
    0
}

fun TeamCityConfigProperties.toJobParameters() = servers.map { serverConfig ->
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
}