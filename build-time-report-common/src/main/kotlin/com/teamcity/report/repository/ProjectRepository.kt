package com.teamcity.report.repository

import com.teamcity.report.repository.entity.ProjectEntity
import com.teamcity.report.repository.entity.ProjectEntityKey
import org.springframework.data.cassandra.repository.Query
import org.springframework.data.repository.Repository
import org.springframework.data.repository.query.Param

/**
 * @author Dmitry Zhuravlev
 *         Date:  24.10.2017
 */
interface ProjectRepository : Repository<ProjectEntity, ProjectEntityKey> {
    fun save(project: ProjectEntity): ProjectEntity

    @Query("select * from report.teamcity_project where serverName=:serverName limit :lim")
    fun getProjects(@Param("serverName") serverName: String, @Param("lim") limit: Long): List<ProjectEntity>
}