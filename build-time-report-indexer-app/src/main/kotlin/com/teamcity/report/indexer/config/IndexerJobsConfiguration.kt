package com.teamcity.report.indexer.config

import com.teamcity.report.indexer.batch.IndexerJobsCoordinatorService
import com.teamcity.report.indexer.batch.processor.BuildsIndexerProcessor
import com.teamcity.report.indexer.batch.processor.ProjectsIndexerProcessor
import com.teamcity.report.indexer.batch.reader.BuildsActualizationIndexerReader
import com.teamcity.report.indexer.batch.reader.BuildsIndexerReader
import com.teamcity.report.indexer.batch.reader.ProjectsActualizationIndexerReader
import com.teamcity.report.indexer.batch.writer.BuildsIndexerWriter
import com.teamcity.report.indexer.batch.writer.ProjectsIndexerWriter
import com.teamcity.report.indexer.client.model.Build
import com.teamcity.report.indexer.client.model.Project
import com.teamcity.report.indexer.converters.toJobParameters
import com.teamcity.report.indexer.properties.TeamCityConfigProperties
import com.teamcity.report.repository.ServerRepository
import com.teamcity.report.repository.entity.BuildEntity
import com.teamcity.report.repository.entity.BuildTypeEntity
import com.teamcity.report.repository.entity.ProjectEntity
import com.teamcity.report.repository.entity.ServerEntity
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
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.TaskExecutor
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import javax.annotation.PostConstruct


/**
 * @author Dmitry Zhuravlev
 *         Date:  20.10.2017
 */

@ComponentScan("com.teamcity.report")
@EnableCassandraRepositories("com.teamcity.report.repository")
@Configuration
@EnableBatchProcessing
@EnableScheduling
class IndexerJobsConfiguration {
    @Autowired
    lateinit var jobBuilderFactory: JobBuilderFactory

    @Autowired
    lateinit var stepBuilderFactory: StepBuilderFactory

    @Autowired
    lateinit var serversConfig: TeamCityConfigProperties

    @Autowired
    lateinit var indexerJobsCoordinatorService: IndexerJobsCoordinatorService

    @Autowired
    lateinit var buildsActualizationReader: BuildsActualizationIndexerReader

    @Autowired
    lateinit var projectsActualizationReader: ProjectsActualizationIndexerReader

    @Autowired
    lateinit var buildsIndexerReader: BuildsIndexerReader

    @Autowired
    lateinit var buildsIndexerWriter: BuildsIndexerWriter

    @Autowired
    lateinit var projectsIndexerWriter: ProjectsIndexerWriter

    @Autowired
    lateinit var buildsIndexerProcessor: BuildsIndexerProcessor

    @Autowired
    lateinit var projectsIndexerProcessor: ProjectsIndexerProcessor

    @Autowired
    lateinit var serverRepository: ServerRepository


    @PostConstruct
    fun saveServers() = serversConfig.servers
            .map { server -> ServerEntity(server.name) }
            .run { serverRepository.saveAll(this) }


    @Bean
    fun taskExecutor(): TaskExecutor = ThreadPoolTaskExecutor().apply {
        val numberOfServers = serversConfig.servers.size
        corePoolSize = 5 * numberOfServers
        maxPoolSize = 10 * numberOfServers
        setQueueCapacity(25 * numberOfServers)
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
    fun allJobs() = buildsIndexerJobs() + buildsIndexerActualizationJobs() + projectsIndexerActualizationJobs()


    private fun buildsIndexerJobs(): List<Job> =
            serversConfig.servers.map { serverConfig ->
                jobBuilderFactory.get("buildsIndexerJob${serverConfig.id}")
                        .incrementer(RunIdIncrementer())
                        .listener(indexerJobsCoordinatorService)
                        .start(buildsIndexerStep(serverConfig))
                        .build()
            }

    private fun buildsIndexerActualizationJobs(): List<Job> =
            serversConfig.servers.map { serverConfig ->
                jobBuilderFactory.get("buildsIndexerActualizationJob${serverConfig.id}")
                        .incrementer(RunIdIncrementer())
                        .listener(indexerJobsCoordinatorService)
                        .start(buildsActualizationIndexerStep(serverConfig))
                        .build()
            }

    private fun projectsIndexerActualizationJobs(): List<Job> =
            serversConfig.servers.map { serverConfig ->
                jobBuilderFactory.get("projectsIndexerActualizationJob${serverConfig.id}")
                        .incrementer(RunIdIncrementer())
                        .listener(indexerJobsCoordinatorService)
                        .start(projectsActualizationIndexerStep(serverConfig))
                        .build()
            }

    private fun buildsActualizationIndexerStep(serverConfig: TeamCityConfigProperties.ServerConfig) =
            stepBuilderFactory.get("buildsActualizationIndexerStep")
                    .chunk<List<Build>?, List<Pair<BuildTypeEntity, BuildEntity>>?>(serverConfig.worker.commitInterval)
                    .reader(buildsActualizationReader)
                    .processor(buildsIndexerProcessor)
                    .writer(buildsIndexerWriter)
                    .allowStartIfComplete(true)
                    .build()

    private fun projectsActualizationIndexerStep(serverConfig: TeamCityConfigProperties.ServerConfig) =
            stepBuilderFactory.get("projectsActualizationIndexerStep")
                    .chunk<List<Project>?, List<ProjectEntity>?>(serverConfig.worker.commitInterval)
                    .reader(projectsActualizationReader)
                    .processor(projectsIndexerProcessor)
                    .writer(projectsIndexerWriter)
                    .allowStartIfComplete(true)
                    .build()


    private fun buildsIndexerStep(serverConfig: TeamCityConfigProperties.ServerConfig) =
            stepBuilderFactory.get("buildsIndexerStep")
                    .chunk<List<Build>?, List<Pair<BuildTypeEntity, BuildEntity>>?>(serverConfig.worker.commitInterval)
                    .reader(buildsIndexerReader)
                    .processor(buildsIndexerProcessor)
                    .writer(buildsIndexerWriter)
                    .allowStartIfComplete(true)
                    .build()
}