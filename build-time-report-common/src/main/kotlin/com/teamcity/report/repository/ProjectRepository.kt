package com.teamcity.report.repository

import com.teamcity.report.repository.entity.ProjectEntity
import com.teamcity.report.repository.entity.ProjectEntityKey
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.cassandra.core.query.CassandraPageRequest
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.cassandra.repository.Query
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Component

/**
 * @author Dmitry Zhuravlev
 *         Date:  24.10.2017
 */
interface ProjectRepository : CassandraRepository<ProjectEntity, ProjectEntityKey> {
    @Query("select * from report.teamcity_project where serverName=:serverName")
    fun getProjects(@Param("serverName") serverName: String, pageable: Pageable): Slice<ProjectEntity>

    @Query("select count(*) from report.teamcity_project where serverName=:serverName")
    fun count(@Param("serverName") serverName: String): Int
}

@Component
class PageableProjectRepository {
    @Autowired
    lateinit var projectRepository: ProjectRepository

    fun getProjects(serverName: String, page: Int, size: Int): List<ProjectEntity> {
        var projectsSlice = projectRepository.getProjects(serverName, CassandraPageRequest.of(0, size))
        for (i in 1..page) {
            if (projectsSlice.hasNext()) {
                projectsSlice = projectRepository.getProjects(serverName, projectsSlice.nextPageable())
            } else return emptyList()
        }
        return projectsSlice.content
    }

    fun count(serverName: String) = projectRepository.count(serverName)
}