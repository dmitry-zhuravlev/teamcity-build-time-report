package com.teamcity.report.indexer.client.model

import com.fasterxml.jackson.annotation.JsonFormat
import com.teamcity.report.indexer.converters.Constants.DATE_PATTERN
import java.time.ZonedDateTime

/**
 * @author Dmitry Zhuravlev
 *         Date:  19.10.2017
 */
//common model
abstract class ElementCollection<out T>(val elements: List<T>, val nextHref: String? = null)


//builds response model
data class Builds(val count: Long, val build: List<Build>) : ElementCollection<Build>(build)

data class Build(val id: Long, val buildType: BuildType,
                 @JsonFormat(pattern = DATE_PATTERN) val finishDate: ZonedDateTime,
                 val statistics: Statistics)

data class BuildType(val id: String, val name: String, val projectId: String)

data class Statistics(val property: List<Property>)

data class Property(val name: String, val value: String)

//projects response model
data class Projects(val project: List<Project>) : ElementCollection<Project>(project)

data class Project(val id: String, val name: String, val parentProjectId: String = ROOT_PARENT_PROJECT_ID)

const val ROOT_PARENT_PROJECT_ID = "_Root"