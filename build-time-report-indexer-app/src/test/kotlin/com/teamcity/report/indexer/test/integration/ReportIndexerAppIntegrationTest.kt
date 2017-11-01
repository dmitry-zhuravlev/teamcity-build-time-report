package com.teamcity.report.indexer.test.integration

import com.teamcity.report.indexer.converters.toJobParameters
import com.teamcity.report.indexer.properties.TeamCityConfigProperties
import com.teamcity.report.repository.BuildRepository
import com.teamcity.report.repository.BuildTypeRepository
import com.teamcity.report.repository.ProjectRepository
import com.teamcity.report.repository.ServerRepository
import org.hamcrest.Matchers.hasItems
import org.hamcrest.Matchers.hasSize
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThat
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

    @Autowired
    lateinit var buildRepository: BuildRepository

    @Autowired
    lateinit var buildTypeRepository: BuildTypeRepository

    @Autowired
    lateinit var projectRepository: ProjectRepository

    @Before
    fun runJobs() {
        doRunJobs()
    }

    @After
    fun cleanup() {
        doCleanup()
    }

    private fun doRunJobs() {
        allJobs.forEach { job ->
            serversConfig.toJobParameters().forEach { jobParameters ->
                jobLauncher.run(job, jobParameters)
            }
        }
    }

    private fun doCleanup() {
        serverRepository.deleteAll()
        buildRepository.deleteAll()
        buildTypeRepository.deleteAll()
        projectRepository.deleteAll()
    }

    @Test
    fun testReportIndexerJobsExecution() {
        val servers = serverRepository.findAll()
        assertThat(servers, hasSize(1))
        assertEquals(servers.first().serverName, "Mock TeamCity Server")
        val builds = buildRepository.findAll()
        assertThat(builds, hasSize(13))

        val returnedBuildsIds = builds.map { build -> build.key.id }
        val buildsIds = (1L..13L).toList().toTypedArray()
        assertThat(returnedBuildsIds, hasItems(*buildsIds))

        val buildTypes = buildTypeRepository.findAll()
        assertThat(buildTypes, hasSize(3))

        val returnedBuildTypes = buildTypes.map { buildType -> buildType.key.buildTypeId }
        val buildTypesIds = arrayOf("CassandraCluster_Build", "DatabasePinger_Build", "DatabasePinger_CassandraClusterForDatabasePinger_Build")
        assertThat(returnedBuildTypes, hasItems(*buildTypesIds))

        val projects = projectRepository.findAll()
        assertThat(projects, hasSize(3))// should be 3 since we don't store <Root project>

        val returnedProjectIds = projects.map { project -> project.key.id }
        val projectsIds = arrayOf("CassandraCluster", "DatabasePinger", "DatabasePinger_CassandraClusterForDatabasePinger")
        assertThat(returnedProjectIds, hasItems(*projectsIds))
    }
}