package com.teamcity.report.batch.writer

import com.teamcity.report.repository.ProjectRepository
import com.teamcity.report.repository.entity.ProjectEntity
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.item.ItemWriter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * @author Dmitry Zhuravlev
 *         Date:  24.10.2017
 */
@Component
@StepScope
class ProjectsIndexerWriter(
        @Autowired
        private val repository: ProjectRepository
) : ItemWriter<List<ProjectEntity>?> {

    override fun write(items: MutableList<out List<ProjectEntity>?>?) {
        items?.forEach { projects ->
            if (projects != null) repository.saveAll(projects)
        }
    }
}