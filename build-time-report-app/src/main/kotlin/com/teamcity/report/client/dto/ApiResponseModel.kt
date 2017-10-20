package com.teamcity.report.client.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.teamcity.report.repository.entity.BuildEntity
import java.time.ZonedDateTime

/**
 * @author Dmitry Zhuravlev
 *         Date:  19.10.2017
 */
data class Builds(val count: Long, val nextHref: String?, val build: List<Build>)

data class Build(var serverName: String? = null,
                 val id: Long, val buildType: BuildType,
                 @JsonFormat(pattern = "yyyyMMdd'T'HHmmssZ") val finishDate: ZonedDateTime,
                 val statistics: Statistics)

data class BuildType(val id: String, val name: String, val projectId: String)

data class Statistics(val property: List<Property>)

data class Property(val name: String, val value: String)

fun Build.toEntity() = BuildEntity(id, buildType.projectId, buildType.id, statistics.property.firstOrNull { it.name == "BuildDuration" }?.value?.toDouble() ?: 0.0) //TODO perform additional checks
