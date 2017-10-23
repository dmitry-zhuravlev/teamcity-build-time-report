package com.teamcity.report.batch.writer

import com.teamcity.report.client.dto.Build
import com.teamcity.report.converters.toEntity
import com.teamcity.report.repository.BuildRepository
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.item.ItemWriter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.retry.backoff.ThreadWaitSleeper
import org.springframework.stereotype.Component

/**
 * @author Dmitry Zhuravlev
 *         Date:  19.10.2017
 */
@Component
@StepScope
class BuildsIndexerWriter(@Value("#{jobParameters['requestTimeoutMs']}")
                          private val requestTimeoutMs: Long,

                          @Autowired
                          private val repository: BuildRepository
) : ItemWriter<List<Build>?> {
    override fun write(items: MutableList<out List<Build>?>?) {
        items?.forEach { item ->
            item?.map { build -> build.toEntity() }?.forEach { build ->
                repository.save(build)
            }
        }
        ThreadWaitSleeper().sleep(requestTimeoutMs)
    }
}