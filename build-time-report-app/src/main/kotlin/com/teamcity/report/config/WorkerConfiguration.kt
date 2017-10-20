package com.teamcity.report.config

import com.teamcity.report.batch.processor.ChunkedBuildsItemProcessor
import com.teamcity.report.batch.reader.ChunkedBuildsItemReader
import com.teamcity.report.batch.writer.BuildsItemWriter
import com.teamcity.report.client.TeamCityApiClientImpl
import com.teamcity.report.client.dto.Build
import com.teamcity.report.repository.BuildRepository
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


/**
 * @author Dmitry Zhuravlev
 *         Date:  20.10.2017
 */

@Configuration
@EnableBatchProcessing
class WorkerConfiguration {
    @Autowired
    lateinit var jobBuilderFactory: JobBuilderFactory

    @Autowired
    lateinit var stepBuilderFactory: StepBuilderFactory

    @Autowired
    lateinit var serversConfig: TeamCityServerConfig

    @Autowired
    lateinit var workerConfig: WorkerConfig

    @Autowired
    lateinit var repository: BuildRepository

//    @Autowired
//    lateinit var apiClient: TeamCityApiClient

    @Bean
    fun buildsIndexerJob() = serversConfig.servers.map { serversConfig ->
        jobBuilderFactory.get("buildsIndexerJob")
                .incrementer(RunIdIncrementer())
                .start(buildsIndexerStep(serversConfig))
                .build()
    }.toList()

    fun buildsIndexerStep(serversConfig: TeamCityServerConfig.Server) = stepBuilderFactory.get("buildsIndexerStep")
            .chunk<List<Build>?, List<Build>?>(workerConfig.chunkSize)
            .reader(ChunkedBuildsItemReader(TeamCityApiClientImpl(serversConfig), workerConfig))
//            .processor { items ->
//                if (items != null)
//                    items.forEach { println("$it") } //TODO specify processor
//                items
//            }
            .processor(ChunkedBuildsItemProcessor(serversConfig.name))
            .writer(BuildsItemWriter(repository, serversConfig.name))
            .build()

}