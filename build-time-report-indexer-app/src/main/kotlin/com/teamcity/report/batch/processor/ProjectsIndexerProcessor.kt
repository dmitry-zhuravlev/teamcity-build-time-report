package com.teamcity.report.batch.processor

import com.teamcity.report.client.dto.Project
import com.teamcity.report.client.dto.ROOT_PARENT_PROJECT_ID
import com.teamcity.report.converters.toEntity
import com.teamcity.report.repository.entity.ProjectEntity
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.item.ItemProcessor
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
 * @author Dmitry Zhuravlev
 *         Date:  24.10.2017
 */
@Component
@StepScope
class ProjectsIndexerProcessor(
        @Value("#{jobParameters['serverName']}")
        val serverName: String
) : ItemProcessor<List<Project>?, List<ProjectEntity>?> {
    override fun process(projects: List<Project>?): List<ProjectEntity>? = if (projects == null) null else with(projects) {
        projects.filterNot { project -> project.id == ROOT_PARENT_PROJECT_ID }
                .map { project -> project.toEntity(serverName) }
    }
}
