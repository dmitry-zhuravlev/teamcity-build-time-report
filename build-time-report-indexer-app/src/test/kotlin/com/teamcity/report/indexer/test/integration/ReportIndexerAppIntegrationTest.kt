package com.teamcity.report.indexer.test.integration

import com.teamcity.report.indexer.converters.toJobParameters
import com.teamcity.report.indexer.properties.TeamCityConfigProperties
import com.teamcity.report.repository.ServerRepository
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.batch.core.Job
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.junit4.SpringRunner

/**
 * @author Dmitry Zhuravlev
 *         Date:  31.10.2017
 */
@RunWith(SpringRunner::class)
@IntegrationTest
class ReportIndexerAppIntegrationTest {
    @Autowired
    lateinit var serversConfig: TeamCityConfigProperties

    @Autowired
    lateinit var allJobs: List<Job>

    @Autowired
    lateinit var jobLauncher: JobLauncher

    @Autowired
    lateinit var serverRepository: ServerRepository

    @Before
    fun runJobs() {
        allJobs.forEach { job ->
            serversConfig.toJobParameters().forEach { jobParameters ->
                jobLauncher.run(job, jobParameters)
            }
        }
    }

    @Test
    fun test() {
        //TODO check stored entities in cassandra "report_test" keyspace
    }
}