package com.teamcity.report.repository.entity

import org.springframework.data.annotation.Id
import org.springframework.data.cassandra.mapping.Table

/**
 * @author Dmitry Zhuravlev
 *         Date:  20.10.2017
 */
@Table("teamcity_build")
data class BuildEntity(@Id val id: Long,
        /*@PrimaryKeyColumn*/ val buildTypeId: String,
        /*@PrimaryKeyColumn*/ val projectId: String,
        /*@PrimaryKeyColumn*/ val buildDuration: Double,
                       var serverName: String? = null)