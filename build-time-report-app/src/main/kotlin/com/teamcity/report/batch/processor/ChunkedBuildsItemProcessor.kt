package com.teamcity.report.batch.processor

import com.teamcity.report.client.dto.Build
import com.teamcity.report.client.dto.toEntity
import org.springframework.batch.item.ItemProcessor

/**
 * @author Dmitry Zhuravlev
 *         Date:  20.10.2017
 */
class ChunkedBuildsItemProcessor(private val server: String) : ItemProcessor<List<Build>?, List<Build>?> {
    override fun process(builds: List<Build>?) = if (builds == null) null else {
        builds.map { build -> build.toEntity().apply { serverName = server } }.toList()
        builds
    }
}