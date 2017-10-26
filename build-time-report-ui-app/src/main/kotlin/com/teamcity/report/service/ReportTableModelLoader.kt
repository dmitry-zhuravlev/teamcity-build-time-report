package com.teamcity.report.service

import com.teamcity.report.model.ReportTableNode
import com.teamcity.report.repository.BuildRepository
import com.teamcity.report.repository.BuildTypeRepository
import com.teamcity.report.repository.ProjectRepository
import com.teamcity.report.repository.entity.BuildTypeEntity
import com.teamcity.report.repository.entity.ProjectEntity
import com.teamcity.report.repository.entity.ROOT_PARENT_PROJECT_ID
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

    fun loadReportModel(serverName: String, limit: Long, beforeFinishDate: String, afterFinishDate: String): List<ReportTableNode> {
        val projectsByIdMap = projectRepository.getProjects(serverName, limit)
                .map { project -> project.key.id to project }.toMap()
        val buildTypesByIdMap = buildTypeRepository.getBuildTypesByProjectIdsAndServerNames(projectsByIdMap.keys.toList(), serverName, limit)
                .map { buildType -> (buildType.key.buildTypeId) to buildType }.toMap()

        val result = mutableMapOf<String, ReportTableNode>()

        projectsByIdMap.values.forEach { project ->
            result[project.key.id] = project.toTableNode().apply { parentId = project.key.parentProjectId }
        }

        buildTypesByIdMap.values.forEach { buildType ->
            val buildTypeNode = buildType.toTableNode().apply { parentId = result[buildType.key.projectId]?.parentId ?: ROOT_PARENT_PROJECT_ID }
            buildTypeNode.duration = buildRepository.sumBuildDurations(buildType.key.buildTypeId, buildType.key.projectId, serverName, beforeFinishDate, afterFinishDate)
            result[buildType.key.projectId]?.childrens?.add(buildTypeNode)
        }

        result.values.forEach { item ->
            val possibleParent = result[item.parentId]
            possibleParent?.childrens?.add(item)
        }

        return result.values.filter { it.parentId == ROOT_PARENT_PROJECT_ID }.toList()
    }

    private fun BuildTypeEntity.toTableNode() = ReportTableNode(buildTypeName)

    private fun ProjectEntity.toTableNode() = ReportTableNode(name)

}