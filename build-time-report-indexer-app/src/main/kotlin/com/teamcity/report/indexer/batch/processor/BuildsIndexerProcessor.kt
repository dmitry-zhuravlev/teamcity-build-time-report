package com.teamcity.report.indexer.batch.processor

import com.teamcity.report.indexer.client.model.Build
import com.teamcity.report.indexer.converters.toEntity
import com.teamcity.report.repository.entity.BuildEntity
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.item.ItemProcessor
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
 * @author Dmitry Zhuravlev
 *         Date:  20.10.2017
 */
@Component
@StepScope
class BuildsIndexerProcessor(
        @Value("#{jobParameters['serverName']}")
        val serverName: String
) : ItemProcessor<List<Build>?, List<BuildEntity>?> {
    override fun process(builds: List<Build>?) = if (builds == null) null else with(builds) {
        builds.map { build -> build.toEntity(serverName) }
    }
}