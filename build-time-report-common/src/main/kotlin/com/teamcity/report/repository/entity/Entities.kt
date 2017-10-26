package com.teamcity.report.repository.entity

import org.springframework.cassandra.core.PrimaryKeyType.CLUSTERED
import org.springframework.cassandra.core.PrimaryKeyType.PARTITIONED
import org.springframework.data.cassandra.mapping.PrimaryKey
import org.springframework.data.cassandra.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.mapping.Table
import java.io.Serializable

/**
 * @author Dmitry Zhuravlev
 *         Date:  20.10.2017
 */
@Table("teamcity_build")
data class BuildEntity(
        @PrimaryKey
        val key: BuildEntityKey,
        val buildDuration: Long)

@PrimaryKeyClass
data class BuildEntityKey(
        @PrimaryKeyColumn(type = PARTITIONED)
        val buildTypeId: String,
        @PrimaryKeyColumn(type = CLUSTERED)
        val projectId: String,
        @PrimaryKeyColumn(type = CLUSTERED)
        val finishDate: Long,
        @PrimaryKeyColumn(type = CLUSTERED)
        val id: Long,
        @PrimaryKeyColumn(type = CLUSTERED)
        val serverName: String) : Serializable

@Table("teamcity_build_type")
data class BuildTypeEntity(
        @PrimaryKey
        val key: BuildTypeEntityKey,
        val buildTypeName: String)

@PrimaryKeyClass
data class BuildTypeEntityKey(
        @PrimaryKeyColumn(type = PARTITIONED)
        val serverName: String,
        @PrimaryKeyColumn(type = CLUSTERED)
        val projectId: String,
        @PrimaryKeyColumn(type = CLUSTERED)
        val buildTypeId: String) : Serializable

@Table("teamcity_project")
data class ProjectEntity(
        @PrimaryKey
        val key: ProjectEntityKey,
        val name: String)

@PrimaryKeyClass
data class ProjectEntityKey(
        @PrimaryKeyColumn(type = PARTITIONED)
        val serverName: String,
        @PrimaryKeyColumn(type = CLUSTERED)
        val id: String,
        @PrimaryKeyColumn(type = CLUSTERED)
        val parentProjectId: String = ROOT_PARENT_PROJECT_ID) : Serializable

const val ROOT_PARENT_PROJECT_ID = "_Root"