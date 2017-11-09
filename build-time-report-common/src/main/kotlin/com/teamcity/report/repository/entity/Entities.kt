package com.teamcity.report.repository.entity

import org.springframework.data.cassandra.core.cql.PrimaryKeyType.CLUSTERED
import org.springframework.data.cassandra.core.cql.PrimaryKeyType.PARTITIONED
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.io.Serializable

/**
 * @author Dmitry Zhuravlev
 *         Date:  20.10.2017
 */

@Table("teamcity_build_view")
data class BuildViewEntity(
        @PrimaryKey
        val key: BuildViewEntityKey,
        val buildDuration: Long)

@PrimaryKeyClass
data class BuildViewEntityKey(
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


@Table("teamcity_build")
data class BuildEntity(
        @PrimaryKey
        val key: BuildEntityKey,
        val buildDuration: Long,
        val projectId: String)

@PrimaryKeyClass
data class BuildEntityKey(
        @PrimaryKeyColumn(type = PARTITIONED)
        val buildTypeId: String,
        @PrimaryKeyColumn(type = CLUSTERED)
        val finishDate: Long,
        @PrimaryKeyColumn(type = CLUSTERED)
        val id: Long,
        @PrimaryKeyColumn(type = CLUSTERED)
        val serverName: String) : Serializable

@Table("teamcity_build_type_view")
data class BuildTypeViewEntity(
        @PrimaryKey
        val key: BuildTypeViewEntityKey,
        val buildTypeName: String)

@PrimaryKeyClass
data class BuildTypeViewEntityKey(
        @PrimaryKeyColumn(type = PARTITIONED)
        val serverName: String,
        @PrimaryKeyColumn(type = CLUSTERED)
        val projectId: String,
        @PrimaryKeyColumn(type = CLUSTERED)
        val buildTypeId: String) : Serializable

@Table("teamcity_build_type")
data class BuildTypeEntity(
        @PrimaryKey
        val key: BuildTypeEntityKey,
        val buildTypeName: String,
        val projectId: String)

@PrimaryKeyClass
data class BuildTypeEntityKey(
        @PrimaryKeyColumn(type = PARTITIONED)
        val serverName: String,
        @PrimaryKeyColumn(type = CLUSTERED)
        val buildTypeId: String) : Serializable

@Table("teamcity_project")
data class ProjectEntity(
        @PrimaryKey
        val key: ProjectEntityKey,
        val name: String,
        val parentProjectId: String = ROOT_PARENT_PROJECT_ID)

@PrimaryKeyClass
data class ProjectEntityKey(
        @PrimaryKeyColumn(type = PARTITIONED)
        val serverName: String,
        @PrimaryKeyColumn(type = CLUSTERED)
        val id: String) : Serializable

@Table("teamcity_server")
data class ServerEntity(
        @PrimaryKey
        val serverName: String)

const val ROOT_PARENT_PROJECT_ID = "_Root"