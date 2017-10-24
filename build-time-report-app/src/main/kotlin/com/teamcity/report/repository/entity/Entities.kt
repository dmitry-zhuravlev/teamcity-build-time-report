package com.teamcity.report.repository.entity

import com.teamcity.report.client.dto.ROOT_PARENT_PROJECT_ID
import org.springframework.data.annotation.Id
import org.springframework.data.cassandra.mapping.Table

/**
 * @author Dmitry Zhuravlev
 *         Date:  20.10.2017
 */
@Table("teamcity_build")
data class BuildEntity(@Id val id: Long,
        /*@PrimaryKeyColumn*/ val buildTypeId: String,
        /*@PrimaryKeyColumn*/ val buildTypeName: String,
        /*@PrimaryKeyColumn*/ val projectId: String,
        /*@PrimaryKeyColumn*/ val finishDate: Long,
        /*@PrimaryKeyColumn*/ val buildDuration: Long,
                       val serverName: String)


@Table("teamcity_project")
data class ProjectEntity(@Id val id: String,
                         val name: String,
                         val parentProjectId: String = ROOT_PARENT_PROJECT_ID,
                         val serverName: String)