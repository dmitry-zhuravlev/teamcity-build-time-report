package com.teamcity.report.config

import com.teamcity.report.batch.IndexerJobsCoordinatorService
import com.teamcity.report.batch.processor.ServerNameEnhancerBuildsProcessor
import com.teamcity.report.batch.reader.BuildsIndexerReader
import com.teamcity.report.batch.writer.BuildsIndexerWriter
import com.teamcity.report.client.TeamCityApiClient
import com.teamcity.report.client.dto.Build
import com.teamcity.report.repository.BuildRepository
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
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.TaskExecutor
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor


/**
 * @author Dmitry Zhuravlev
 *         Date:  20.10.2017
 */

@Configuration
@EnableBatchProcessing
@EnableAsync
class WorkerConfiguration /*: DefaultBatchConfigurer()*/ {
    @Autowired
    lateinit var jobBuilderFactory: JobBuilderFactory

    @Autowired
    lateinit var stepBuilderFactory: StepBuilderFactory

    @Autowired
    lateinit var serversConfig: TeamCityConfig

    @Autowired
    lateinit var repository: BuildRepository

    @Autowired
    lateinit var client: TeamCityApiClient

    @Autowired
    lateinit var indexerJobsCoordinatorService: IndexerJobsCoordinatorService


    @Bean
    fun taskExecutor(): TaskExecutor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 5    //TODO choose in respect of number of servers
        executor.maxPoolSize = 10
        executor.setQueueCapacity(25)
        return executor
    }


    @Bean
    fun jobLauncher(jobRepository: JobRepository) = SimpleJobLauncher().apply {
        setTaskExecutor(taskExecutor())
        setJobRepository(jobRepository)
    }

    @Bean
    fun jobRegistryBeanPostProcessor(jobRegistry: JobRegistry, jobLauncher: JobLauncher, allJobs:List<Job>): JobRegistryBeanPostProcessor {
        val jobRegistryBeanPostProcessor = JobRegistryBeanPostProcessor()
        jobRegistryBeanPostProcessor.setJobRegistry(jobRegistry)
        allJobs.forEach { job ->
            jobRegistryBeanPostProcessor.postProcessAfterInitialization(job, job.name)
        }

        return jobRegistryBeanPostProcessor
    }


    @Bean
    fun allJobs() = buildsIndexerJobs() + indexerActualizationJobs()


    private fun buildsIndexerJobs() =
            serversConfig.servers.map { serverConfig ->
                val buildIndexerStep = buildsIndexerStep(serverConfig)
                jobBuilderFactory.get("buildsIndexerJob${serverConfig.name.replace(" ", "")}") //TODO possible to use id instead serverName
                        .incrementer(RunIdIncrementer())
                        .start(buildIndexerStep)
                        .build()
            }

    private fun indexerActualizationJobs() =
            serversConfig.servers.map { serverConfig ->
                jobBuilderFactory.get("buildsIndexerActualizationJob${serverConfig.name.replace(" ", "")}") //TODO possible to use id instead serverName
                        .incrementer(RunIdIncrementer())
                        .start(actualizationIndexerStep())
                        .listener(indexerJobsCoordinatorService)
                        .build()
            }

    private fun actualizationIndexerStep() =
            stepBuilderFactory.get("actualizationIndexerStep")
                    .tasklet { contribution, chunkContext ->
                        println("actualizationIndexer tasklet executed!") //TODO implement
                        Thread.sleep(4000)
                        RepeatStatus.FINISHED
                    }
                    .allowStartIfComplete(true)
                    .build()


    private fun buildsIndexerStep(serverConfig: TeamCityConfig.ServerConfig) =
            stepBuilderFactory.get("buildsIndexerStep")
                    .chunk<List<Build>?, List<Build>?>(serverConfig.worker.chunkSize)
                    .reader(BuildsIndexerReader(client, serverConfig))
                    .processor(ServerNameEnhancerBuildsProcessor(serverConfig.name))
                    .writer(BuildsIndexerWriter(repository, serverConfig.worker.requestTimeoutMs))
                    .allowStartIfComplete(true)
                    .build()

}