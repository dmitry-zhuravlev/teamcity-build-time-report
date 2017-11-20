package com.teamcity.report.ui.service

import com.teamcity.report.repository.BuildRepository
import com.teamcity.report.repository.PageableBuildTypeViewRepository
import com.teamcity.report.repository.PageableProjectRepository
import com.teamcity.report.repository.entity.BuildTypeViewEntity
import com.teamcity.report.repository.entity.ProjectEntity
import com.teamcity.report.repository.entity.ROOT_PARENT_PROJECT_ID
import com.teamcity.report.ui.model.ReportNode
import com.vaadin.spring.annotation.SpringComponent
import org.springframework.beans.factory.annotation.Autowired
import java.io.Serializable

/**
 * @author Dmitry Zhuravlev
 *         Date:  26.10.2017
 */
@SpringComponent
class ReportModelLoader : Serializable {
    @Autowired
    lateinit var buildRepository: BuildRepository

    @Autowired
    lateinit var buildTypeRepository: PageableBuildTypeViewRepository

    @Autowired
    lateinit var projectRepository: PageableProjectRepository

    fun loadReportModel(serverName: String, beforeFinishDate: Long, afterFinishDate: Long, page: Int? = null, size: Int? = null): List<ReportNode> {
        val projectsByIdMap = projectRepository.getProjects(serverName, page, size)
                .map { project -> project.key.id to project }.toMap()
        val buildTypesByIdMap = buildTypeRepository.getBuildTypesByProjectIdsAndServerNames(projectsByIdMap.keys.toList(), serverName, page, size)
                .map { buildType -> (buildType.key.buildTypeId) to buildType }.toMap()

        val result = hashMapOf<String, ReportNode>()

        projectsByIdMap.values.forEach { project ->
            result[project.key.id] = project.toTableNode()
        }

        buildTypesByIdMap.values.forEach { buildType ->
            val buildTypeNode = buildType.toTableNode()
            buildTypeNode.duration = buildRepository.sumBuildDurations(buildType.key.buildTypeId, serverName, beforeFinishDate, afterFinishDate)
            result[buildType.key.projectId]?.childrens?.add(buildTypeNode)
        }

        result.values.forEach { item ->
            result[item.parentId]?.childrens?.add(item)
        }
        return listOf(ReportNode("_RootTotal", "Total",
                childrens = result.values.filter { it.parentId == ROOT_PARENT_PROJECT_ID }.toMutableList()).apply {
            duration = calculateDuration()
            durationPercentage = calculatePercentageDuration(duration)
        })
    }

    fun loadReportModelFlat(serverName: String, beforeFinishDate: Long, afterFinishDate: Long, page: Int? = null, size: Int? = null)
            = loadReportModel(serverName, beforeFinishDate, afterFinishDate, page, size)[0].childrens.flatCollectAllNodes()

    private fun List<ReportNode>.flatCollectAllNodes(nodeList: MutableList<ReportNode> = mutableListOf()): List<ReportNode> {
        forEach { node ->
            nodeList.add(node)
            node.childrens.flatCollectAllNodes(nodeList)
        }
        return nodeList
    }

    private fun BuildTypeViewEntity.toTableNode() = ReportNode(key.buildTypeId, buildTypeName)

    private fun ProjectEntity.toTableNode() = ReportNode(key.id, name, parentId = parentProjectId)

}