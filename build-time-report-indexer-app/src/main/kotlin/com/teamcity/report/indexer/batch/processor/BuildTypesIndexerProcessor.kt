package com.teamcity.report.indexer.batch.processor

import com.teamcity.report.indexer.client.model.BuildType
import com.teamcity.report.indexer.converters.toEntity
import com.teamcity.report.repository.entity.BuildTypeEntity
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.item.ItemProcessor
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
 * @author Dmitry Zhuravlev
 *         Date: 11/11/2017
 */
@Component
@StepScope
class BuildTypesIndexerProcessor(
        @Value("#{jobParameters['serverName']}")
        val serverName: String
) : ItemProcessor<List<BuildType>?, List<BuildTypeEntity>?> {
    override fun process(buildTypes: List<BuildType>?): List<BuildTypeEntity>? = if (buildTypes == null) null else with(buildTypes) {
        buildTypes.map { buildType -> buildType.toEntity(serverName) }
    }
}