package com.teamcity.report.service

import com.teamcity.report.model.ProjectOrBuild
import com.teamcity.report.repository.BuildRepository
import com.teamcity.report.repository.BuildTypeRepository
import com.teamcity.report.repository.ProjectRepository
import com.vaadin.spring.annotation.SpringComponent
import com.vaadin.spring.annotation.ViewScope
import org.springframework.beans.factory.annotation.Autowired

/**
 * @author Dmitry Zhuravlev
 *         Date:  26.10.2017
 */
@SpringComponent
@ViewScope
class ReportTableModelLoader {
    @Autowired
    lateinit var buildRepository: BuildRepository

    @Autowired
    lateinit var buildTypeRepository: BuildTypeRepository

    @Autowired
    lateinit var projectRepository: ProjectRepository

    fun loadReportModel(serverName: String, limit: Long, beforeFinishDate: String, afterFinishDate: String): List<ProjectOrBuild> {
        //TODO fix draft implementation
        val projectsMap = projectRepository.getProjects(serverName, limit).map { project -> project.key.id to ProjectOrBuild(project.name) }.toMap()
        val buildTypesMap = buildTypeRepository.getBuildTypesByProjectIdsAndServerNames(projectsMap.keys.toList(), serverName, limit)
                .map { buildType -> (buildType.key.buildTypeId to buildType.key.projectId) to ProjectOrBuild(buildType.buildTypeName) }.toMap()
        buildTypesMap.keys.forEach { (buildTypeId, projectId) ->
            buildTypesMap[buildTypeId to projectId]?.duration = buildRepository.sumBuildDurations(buildTypeId, projectId, serverName, beforeFinishDate, afterFinishDate)
        }
        return buildTypesMap.values + projectsMap.values
        /*resultModel.values.forEach {projectOrBuild->
            projectOrBuild
        }*/
    }

}