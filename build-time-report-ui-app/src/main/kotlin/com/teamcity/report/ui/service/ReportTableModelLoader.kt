package com.teamcity.report.ui.service

import com.teamcity.report.repository.BuildRepository
import com.teamcity.report.repository.PageableBuildTypeRepository
import com.teamcity.report.repository.PageableProjectRepository
import com.teamcity.report.repository.entity.BuildTypeEntity
import com.teamcity.report.repository.entity.ProjectEntity
import com.teamcity.report.repository.entity.ROOT_PARENT_PROJECT_ID
import com.teamcity.report.ui.model.ReportTableNode
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
    lateinit var buildTypeRepository: PageableBuildTypeRepository

    @Autowired
    lateinit var projectRepository: PageableProjectRepository

    fun loadReportModel(serverName: String, beforeFinishDate: Long, afterFinishDate: Long, page: Int, size: Int): List<ReportTableNode> {
        val projectsByIdMap = projectRepository.getProjects(serverName, page, size)
                .map { project -> project.key.id to project }.toMap()
        val buildTypesByIdMap = buildTypeRepository.getBuildTypesByProjectIdsAndServerNames(projectsByIdMap.keys.toList(), serverName, page, size)
                .map { buildType -> (buildType.key.buildTypeId) to buildType }.toMap()

        val result = hashMapOf<String, ReportTableNode>()

        projectsByIdMap.values.forEach { project ->
            result[project.key.id] = project.toTableNode().apply { parentId = project.key.parentProjectId }
        }

        buildTypesByIdMap.values.forEach { buildType ->
            val buildTypeNode = buildType.toTableNode()
            buildTypeNode.duration = buildRepository.sumBuildDurations(buildType.key.buildTypeId, buildType.key.projectId, serverName, beforeFinishDate, afterFinishDate)
            result[buildType.key.projectId]?.childrens?.add(buildTypeNode)
        }

        result.values.forEach { item ->
            val possibleParent = result[item.parentId]
            possibleParent?.childrens?.add(item)
        }
        return listOf(ReportTableNode("_RootTotal", "Total",
                childrens = result.values.filter { it.parentId == ROOT_PARENT_PROJECT_ID }.toMutableList()).apply {
            duration = calculateDuration()
            durationPercentage = calculatePercentageDuration(duration)
        })
    }

    private fun BuildTypeEntity.toTableNode() = ReportTableNode(key.buildTypeId, buildTypeName)

    private fun ProjectEntity.toTableNode() = ReportTableNode(key.id, name)

}