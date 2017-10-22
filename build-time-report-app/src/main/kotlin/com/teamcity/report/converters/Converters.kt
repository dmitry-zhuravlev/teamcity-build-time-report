package com.teamcity.report.converters

import com.teamcity.report.client.dto.Build
import com.teamcity.report.repository.entity.BuildEntity
import java.time.ZonedDateTime

/**
 * @author Dmitry Zhuravlev
 *         Date: 22/10/2017
 */

fun Build.toEntity() = BuildEntity(id, buildType.projectId, buildType.id, finishDate.toMilli(),
        statistics.property.firstOrNull { it.name == "BuildDuration" }?.value?.toLong() ?: 0, //TODO perform additional checks
        serverName)

fun ZonedDateTime.toMilli() = toInstant().toEpochMilli()