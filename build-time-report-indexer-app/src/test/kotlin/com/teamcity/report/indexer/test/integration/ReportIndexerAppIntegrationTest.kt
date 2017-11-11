package com.teamcity.report.indexer.test.integration

import com.teamcity.report.indexer.converters.toJobParameters
import com.teamcity.report.indexer.converters.toMilli
import com.teamcity.report.indexer.properties.TeamCityConfigProperties
import com.teamcity.report.indexer.test.constants.TestConstants
import com.teamcity.report.indexer.test.mock.TeamCityServerMockController
import com.teamcity.report.repository.BuildRepository
import com.teamcity.report.repository.BuildTypeRepository
import com.teamcity.report.repository.ProjectRepository
import com.teamcity.report.repository.ServerRepository
import com.teamcity.report.repository.entity.BuildEntity
import com.teamcity.report.repository.entity.BuildTypeEntity
import com.teamcity.report.repository.entity.ProjectEntity
import com.teamcity.report.repository.entity.ServerEntity
import org.hamcrest.Matchers.hasItems
import org.hamcrest.Matchers.hasSize
import org.junit.After
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.junit4.SpringRunner
import java.time.ZonedDateTime.now

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

    @Autowired
    lateinit var teamCityServerMockController: TeamCityServerMockController

    @After
    fun cleanup() {
        doCleanup()
    }

    private fun runJobs() {
        allJobs.forEach { job ->
            serversConfig.toJobParameters()
                    .map { jobParameters ->
                        JobParametersBuilder(jobParameters)
                                .addLong("Timestamp", now().toMilli()) //we need to add some volatile parameter just to be able to run jobs with such parameters again
                                .toJobParameters()
                    }
                    .forEach { jobParameters ->
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
        runJobs()
        var servers = serverRepository.findAll()
        var builds = buildRepository.findAll()
        var buildTypes = buildTypeRepository.findAll()
        var projects = projectRepository.findAll()
        assertBuildAndProjects(servers, projects, buildTypes, builds)

        //configure mock to simulate project and build configs move
        teamCityServerMockController.buildTypesResponseFileName = TestConstants.BUILD_TYPES_AFTER_MOVE_RESPONSE_FILE_NAME
        teamCityServerMockController.projectsResponseFileName = TestConstants.PROJECTS_AFTER_MOVE_RESPONSE_FILE_NAME

        runJobs()
        servers = serverRepository.findAll()
        builds = buildRepository.findAll()
        buildTypes = buildTypeRepository.findAll()
        projects = projectRepository.findAll()
        assertBuildsAndProjectsAfterMove(servers, projects, buildTypes, builds)
    }

    private fun assertBuildAndProjects(servers: List<ServerEntity>, projects: List<ProjectEntity>,
                                       buildTypes: List<BuildTypeEntity>, builds: List<BuildEntity>) {
        //assert servers
        assertThat(servers, hasSize(1))
        assertEquals(servers.first().serverName, "Mock TeamCity Server")

        //assert builds
        assertThat(builds, hasSize(13))
        val returnedBuildsIds = builds.map { build -> build.key.id }
        val buildsIds = (1L..13L).toList().toTypedArray()
        assertThat(returnedBuildsIds, hasItems(*buildsIds))

        //assert buildTypes
        assertThat(buildTypes, hasSize(3))
        val returnedBuildTypes = buildTypes.map { buildType -> buildType.key.buildTypeId }
        val buildTypesIds = arrayOf("CassandraCluster_Build", "DatabasePinger_Build", "DatabasePinger_CassandraClusterForDatabasePinger_Build")
        assertThat(returnedBuildTypes, hasItems(*buildTypesIds))

        //assert projects
        assertThat(projects, hasSize(3))// should be 3 since we don't store <Root project>
        val returnedProjectIds = projects.map { project -> project.key.id }
        val projectsIds = arrayOf("CassandraCluster", "DatabasePinger", "DatabasePinger_CassandraClusterForDatabasePinger")
        assertThat(returnedProjectIds, hasItems(*projectsIds))
    }

    private fun assertBuildsAndProjectsAfterMove(servers: List<ServerEntity>, projects: List<ProjectEntity>,
                                                 buildTypes: List<BuildTypeEntity>, builds: List<BuildEntity>) {
        assertBuildAndProjects(servers, projects, buildTypes, builds)
        buildTypes.forEach { buildType -> if (buildType.key.buildTypeId == "DatabasePinger_Build") assertTrue(buildType.projectId == "CassandraCluster") }
        projects.forEach { project -> if (project.key.id == "DatabasePinger") assertTrue(project.parentProjectId == "CassandraCluster") }
    }
}