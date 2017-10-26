package com.teamcity.report.batch.processor

import com.teamcity.report.client.dto.Build
import com.teamcity.report.converters.toEntity
import com.teamcity.report.converters.toTypeEntity
import com.teamcity.report.repository.entity.BuildEntity
import com.teamcity.report.repository.entity.BuildTypeEntity
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
) : ItemProcessor<List<Build>?, List<Pair<BuildTypeEntity, BuildEntity>>?> {
    override fun process(builds: List<Build>?) = if (builds == null) null else with(builds) {
        builds.map { build -> build.toTypeEntity(serverName) to build.toEntity(serverName) }
    }
}