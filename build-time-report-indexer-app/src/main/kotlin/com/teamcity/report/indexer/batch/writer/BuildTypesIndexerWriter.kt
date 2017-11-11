package com.teamcity.report.indexer.batch.writer

import com.teamcity.report.repository.BuildTypeRepository
import com.teamcity.report.repository.entity.BuildTypeEntity
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.item.ItemWriter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * @author Dmitry Zhuravlev
 *         Date: 11/11/2017
 */
@Component
@StepScope
class BuildTypesIndexerWriter(
        @Autowired
        private val buildTypesRepository: BuildTypeRepository
) : ItemWriter<List<BuildTypeEntity>?> {

    override fun write(items: MutableList<out List<BuildTypeEntity>?>?) {
        items?.forEach { buildTypes ->
            if (buildTypes != null) {
                buildTypesRepository.saveAll(buildTypes)
            }
        }
    }
}