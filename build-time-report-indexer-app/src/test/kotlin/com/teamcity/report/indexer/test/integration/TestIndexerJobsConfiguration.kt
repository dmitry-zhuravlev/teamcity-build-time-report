package com.teamcity.report.indexer.test.integration

import com.teamcity.report.indexer.config.IndexerJobsConfiguration
import org.springframework.batch.core.Job
import org.springframework.batch.core.configuration.JobRegistry
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.SyncTaskExecutor
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories

/**
 * @author Dmitry Zhuravlev
 *         Date:  31.10.2017
 */
@EnableCassandraRepositories("com.teamcity.report.repository")
@Configuration
@EnableBatchProcessing
class TestIndexerJobsConfiguration : IndexerJobsConfiguration() {
    override fun taskExecutor() = SyncTaskExecutor()

    override fun jobRegistryBeanPostProcessor(jobRegistry: JobRegistry, jobLauncher: JobLauncher, allJobs: List<Job>) = JobRegistryBeanPostProcessor().apply {
        setJobRegistry(jobRegistry)
    }
}