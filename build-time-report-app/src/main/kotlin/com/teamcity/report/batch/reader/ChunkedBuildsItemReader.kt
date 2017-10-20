package com.teamcity.report.batch.reader

import com.teamcity.report.client.TeamCityApiClient
import com.teamcity.report.client.dto.Build
import com.teamcity.report.config.WorkerConfig
import org.springframework.batch.item.ItemReader


/**
 * @author Dmitry Zhuravlev
 *         Date:  19.10.2017
 */
class ChunkedBuildsItemReader(private val client: TeamCityApiClient, private val workerConfig: WorkerConfig) : ItemReader<List<Build>?> {
    var start = workerConfig.startPage
    override fun read() = with(workerConfig) {
        val buildsList = client.getBuilds(chunkSize, start).build
        start += chunkSize
        if (isLastChunk(buildsList)) null else buildsList
    }

    private fun isLastChunk(builds: List<Build>) = builds.isEmpty()
}