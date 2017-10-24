package com.teamcity.report.repository

import com.teamcity.report.repository.entity.ProjectEntity
import org.springframework.data.repository.Repository

/**
 * @author Dmitry Zhuravlev
 *         Date:  24.10.2017
 */
interface ProjectRepository : Repository<ProjectEntity, String> {
    fun save(project: ProjectEntity): ProjectEntity
}