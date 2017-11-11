package com.teamcity.report.indexer.batch.writer

import com.teamcity.report.repository.BuildRepository
import com.teamcity.report.repository.entity.BuildEntity
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.item.ItemWriter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * @author Dmitry Zhuravlev
 *         Date:  19.10.2017
 */
@Component
@StepScope
class BuildsIndexerWriter(
        @Autowired
        private val buildsRepository: BuildRepository
) : ItemWriter<List<BuildEntity>?> {

    override fun write(items: MutableList<out List<BuildEntity>?>?) {
        items?.forEach { builds ->
            if (builds != null) {
                buildsRepository.saveAll(builds)
            }
        }
    }
}