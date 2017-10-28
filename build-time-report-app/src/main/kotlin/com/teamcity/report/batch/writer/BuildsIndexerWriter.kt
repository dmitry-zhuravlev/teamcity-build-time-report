package com.teamcity.report.batch.writer

import com.teamcity.report.repository.BuildRepository
import com.teamcity.report.repository.BuildTypeRepository
import com.teamcity.report.repository.entity.BuildEntity
import com.teamcity.report.repository.entity.BuildTypeEntity
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
        private val buildsRepository: BuildRepository,
        @Autowired
        private val buildTypesRepository: BuildTypeRepository
) : ItemWriter<List<Pair<BuildTypeEntity, BuildEntity>>?> {

    override fun write(items: MutableList<out List<Pair<BuildTypeEntity, BuildEntity>>?>?) {
        items?.forEach { buildsAndBuildTypes ->
            if (buildsAndBuildTypes != null) {
                buildTypesRepository.saveAll(buildsAndBuildTypes.map { (buildType, _) -> buildType })
                buildsRepository.saveAll(buildsAndBuildTypes.map { (_, build) -> build })
            }
        }
    }
}