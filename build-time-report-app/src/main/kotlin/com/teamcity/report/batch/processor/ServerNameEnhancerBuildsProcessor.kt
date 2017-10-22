package com.teamcity.report.batch.processor

import com.teamcity.report.client.dto.Build
import org.springframework.batch.item.ItemProcessor

/**
 * @author Dmitry Zhuravlev
 *         Date:  20.10.2017
 */
class ServerNameEnhancerBuildsProcessor(private val server: String) : ItemProcessor<List<Build>?, List<Build>?> {
    override fun process(builds: List<Build>?) = if (builds == null) null else with(builds) {
        forEach { build -> build.apply { serverName = ServerNameEnhancerBuildsProcessor@ server } }
        builds
    }
}