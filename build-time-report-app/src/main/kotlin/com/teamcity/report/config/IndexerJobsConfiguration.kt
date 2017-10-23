package com.teamcity.report.config

import com.teamcity.report.batch.IndexerJobsCoordinatorService
import com.teamcity.report.batch.processor.ServerNameEnhancerBuildsProcessor
import com.teamcity.report.batch.reader.BuildsActualizationIndexerReader
import com.teamcity.report.batch.reader.BuildsIndexerReader
import com.teamcity.report.batch.writer.BuildsIndexerWriter
import com.teamcity.report.client.TeamCityApiClient
import com.teamcity.report.client.dto.Build
import com.teamcity.report.converters.toJobParameters
import org.springframework.batch.core.Job
import org.springframework.batch.core.configuration.JobRegistry
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.launch.support.SimpleJobLauncher
import org.springframework.batch.core.repository.JobRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor


/**
 * @author Dmitry Zhuravlev
 *         Date:  20.10.2017
 */

@Configuration
@EnableBatchProcessing
@EnableAsync
class IndexerJobsConfiguration /*: DefaultBatchConfigurer()*/ {
    @Autowired
    lateinit var jobBuilderFactory: JobBuilderFactory

    @Autowired
    lateinit var stepBuilderFactory: StepBuilderFactory

    @Autowired
    lateinit var serversConfig: TeamCityConfig

    @Autowired
    lateinit var client: TeamCityApiClient

    @Autowired
    lateinit var indexerJobsCoordinatorService: IndexerJobsCoordinatorService

    @Autowired
    lateinit var buildsActualizationReader: BuildsActualizationIndexerReader

    @Autowired
    lateinit var buildsIndexerReader: BuildsIndexerReader

    @Autowired
    lateinit var buildsIndexerWriter: BuildsIndexerWriter


    @Bean
    fun taskExecutor() = ThreadPoolTaskExecutor().apply {
        corePoolSize = 5    //TODO choose in respect of number of servers
        maxPoolSize = 10
        setQueueCapacity(25)
    }


    @Bean
    fun jobLauncher(jobRepository: JobRepository) = SimpleJobLauncher().apply {
        setTaskExecutor(taskExecutor())
        setJobRepository(jobRepository)
    }

    @Bean
    fun jobRegistryBeanPostProcessor(jobRegistry: JobRegistry, jobLauncher: JobLauncher, allJobs: List<Job>) = JobRegistryBeanPostProcessor().apply {
        setJobRegistry(jobRegistry)
        allJobs.forEach { job ->
            postProcessAfterInitialization(job, job.name)
            serversConfig.toJobParameters().forEach { jobParameters ->
                jobLauncher.run(job, jobParameters)
            }
        }
    }

    @Bean
    fun allJobs() = buildsIndexerJobs() + indexerActualizationJobs()


    private fun buildsIndexerJobs() =
            serversConfig.servers.map { serverConfig ->
                val buildIndexerStep = buildsIndexerStep(serverConfig)
                jobBuilderFactory.get("buildsIndexerJob${serverConfig.id}")
                        .incrementer(RunIdIncrementer())
                        .start(buildIndexerStep)
                        .build()
            }

    private fun indexerActualizationJobs() =
            serversConfig.servers.map { serverConfig ->
                jobBuilderFactory.get("buildsIndexerActualizationJob${serverConfig.id}")
                        .incrementer(RunIdIncrementer())
                        .start(actualizationIndexerStep(serverConfig))
                        .listener(indexerJobsCoordinatorService)
                        .build()
            }

    private fun actualizationIndexerStep(serverConfig: TeamCityConfig.ServerConfig) =
            stepBuilderFactory.get("actualizationIndexerStep")
                    .chunk<List<Build>?, List<Build>?>(serverConfig.worker.chunkSize.toInt())
                    .reader(buildsActualizationReader)
                    .processor(ServerNameEnhancerBuildsProcessor(serverConfig.name))
                    .writer(buildsIndexerWriter)
                    .allowStartIfComplete(true)
                    .build()


    private fun buildsIndexerStep(serverConfig: TeamCityConfig.ServerConfig) =
            stepBuilderFactory.get("buildsIndexerStep")
                    .chunk<List<Build>?, List<Build>?>(serverConfig.worker.chunkSize.toInt())
                    .reader(buildsIndexerReader)
                    .processor(ServerNameEnhancerBuildsProcessor(serverConfig.name))
                    .writer(buildsIndexerWriter)
                    .allowStartIfComplete(true)
                    .build()

}