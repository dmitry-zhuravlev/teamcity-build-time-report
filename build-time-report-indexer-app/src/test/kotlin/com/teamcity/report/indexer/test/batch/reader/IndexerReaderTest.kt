package com.teamcity.report.indexer.test.batch.reader

import com.teamcity.report.indexer.batch.reader.BuildsIndexerReader
import com.teamcity.report.indexer.batch.reader.ProjectsActualizationIndexerReader
import com.teamcity.report.indexer.client.TeamCityApiClient
import com.teamcity.report.indexer.test.config.TestMockServerProperties
import org.hamcrest.Matchers
import org.hamcrest.Matchers.hasSize
import org.junit.Assert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.junit4.SpringRunner

/**
 * @author Dmitry Zhuravlev
 * Date:  31.10.2017
 */
@RunWith(SpringRunner::class)
@ReaderPackageTest
class IndexerReaderTest {

    @Autowired
    lateinit var teamCityApiClient: TeamCityApiClient

    @Autowired
    lateinit var testMockServerProperties: TestMockServerProperties

    @Test
    fun readBuilds() {
        val builds = BuildsIndexerReader(0, 10, 100,
                serverId = testMockServerProperties.id,
                serverName = testMockServerProperties.name,
                serverUrl = testMockServerProperties.url,
                apiVersion = testMockServerProperties.apiVersion,
                userName = testMockServerProperties.username,
                userPassword = testMockServerProperties.password,
                client = teamCityApiClient).read()
        assertThat(builds, hasSize(13))
    }

    @Test
    fun readProjects() {
        val projects = ProjectsActualizationIndexerReader(0, 10, 100,
                serverId = testMockServerProperties.id,
                serverName = testMockServerProperties.name,
                serverUrl = testMockServerProperties.url,
                apiVersion = testMockServerProperties.apiVersion,
                userName = testMockServerProperties.username,
                userPassword = testMockServerProperties.password,
                client = teamCityApiClient).read()
        assertThat(projects, Matchers.hasSize(4))
    }
}