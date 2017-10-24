package com.teamcity.report.client.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.teamcity.report.config.ConfigDefault.DATE_PATTERN
import java.time.ZonedDateTime

/**
 * @author Dmitry Zhuravlev
 *         Date:  19.10.2017
 */
//builds response model
data class Builds(val count: Long, val build: List<Build>, val nextHref: String?)

data class Build(val id: Long, val buildType: BuildType,
                 @JsonFormat(pattern = DATE_PATTERN) val finishDate: ZonedDateTime,
                 val statistics: Statistics)

data class BuildType(val id: String, val name: String, val projectId: String)

data class Statistics(val property: List<Property>)

data class Property(val name: String, val value: String)

//projects response model
data class Projects(val project: List<Project>, val nextHref: String?)

data class Project(val id: String, val name: String, val parentProjectId: String = ROOT_PARENT_PROJECT_ID)

const val ROOT_PARENT_PROJECT_ID = "_Root"