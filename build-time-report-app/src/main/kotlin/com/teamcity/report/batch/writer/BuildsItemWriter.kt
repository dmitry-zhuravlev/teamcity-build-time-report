package com.teamcity.report.batch.writer

import com.teamcity.report.client.dto.Build
import com.teamcity.report.client.dto.toEntity
import com.teamcity.report.repository.BuildRepository
import org.springframework.batch.item.ItemWriter

/**
 * @author Dmitry Zhuravlev
 *         Date:  19.10.2017
 */
class BuildsItemWriter(private val repository: BuildRepository, private val serverName: String) : ItemWriter<List<Build>?> {

    override fun write(items: MutableList<out List<Build>?>?) {
        items?.forEach { item ->
            item?.map { build -> build.toEntity().apply { serverName = this@BuildsItemWriter.serverName } }?.forEach { build ->
                repository.save(build)
            }
        }
    }
}