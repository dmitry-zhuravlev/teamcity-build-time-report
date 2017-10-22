package com.teamcity.report.batch.writer

import com.teamcity.report.client.dto.Build
import com.teamcity.report.converters.toEntity
import com.teamcity.report.repository.BuildRepository
import org.springframework.batch.item.ItemWriter
import org.springframework.retry.backoff.ThreadWaitSleeper

/**
 * @author Dmitry Zhuravlev
 *         Date:  19.10.2017
 */
class BuildsIndexerWriter(private val repository: BuildRepository, private val requestTimeoutMs:Long) : ItemWriter<List<Build>?> {

    override fun write(items: MutableList<out List<Build>?>?) {
        items?.forEach { item ->
            item?.map { build -> build.toEntity()}?.forEach { build ->
                repository.save(build)
            }
        }
        ThreadWaitSleeper().sleep(requestTimeoutMs)
    }
}