package com.teamcity.report.indexer.test.integration

import com.teamcity.report.indexer.config.IndexerJobsConfiguration
import org.springframework.batch.core.Job
import org.springframework.batch.core.configuration.JobRegistry
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.core.task.SyncTaskExecutor
import org.springframework.data.cassandra.core.convert.CassandraCustomConversions
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories
import java.util.*

/**
 * @author Dmitry Zhuravlev
 *         Date:  31.10.2017
 */
@EnableCassandraRepositories("com.teamcity.report.repository")
@Configuration
@EnableBatchProcessing
class IntegrationTestConfiguration : IndexerJobsConfiguration() {
    override fun taskExecutor() = SyncTaskExecutor()

    override fun jobRegistryBeanPostProcessor(jobRegistry: JobRegistry, jobLauncher: JobLauncher, allJobs: List<Job>) = JobRegistryBeanPostProcessor().apply {
        setJobRegistry(jobRegistry)
    }

    @Bean
    fun cassandraCustomConversions(): CassandraCustomConversions {
        return CassandraCustomConversions(listOf(DateToLongConverter()))
    }
}

class DateToLongConverter : Converter<Date, Long> {
    override fun convert(source: Date?): Long? {
        if (source == null) return null
        return source.toInstant().toEpochMilli()
    }
}