package com.teamcity.report.ui.test.service

import com.teamcity.report.repository.entity.BuildTypeEntity
import com.teamcity.report.repository.entity.BuildTypeEntityKey
import com.teamcity.report.repository.entity.ProjectEntity
import com.teamcity.report.repository.entity.ProjectEntityKey
import com.teamcity.report.ui.model.ReportTableNode
import com.teamcity.report.ui.service.ReportTableModelLoader
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.junit4.SpringRunner

/**
 * @author Dmitry Zhuravlev
 * Date:  01.11.2017
 */

@RunWith(SpringRunner::class)
@ServicePackageTest
class ReportTableModelLoaderTest {

    @Autowired
    lateinit var reportTableLoader: ReportTableModelLoader

    companion object {
        const val BUILD_TYPE_RELEASE_NAME = "release"
        const val BUILD_TYPE_NIGHTLY_NAME = "nightly"
        const val SERVER1_NAME = "Server1"
        //project1
        const val PROJECT1_ID = "project1_id"
        const val PROJECT1_NAME = "Project1"
        const val BUILD_TYPE1_PROJECT1_ID = "project1_$BUILD_TYPE_RELEASE_NAME"
        const val BUILD_TYPE2_PROJECT1_ID = "project1_$BUILD_TYPE_NIGHTLY_NAME"
        //project2
        const val PROJECT2_ID = "project2_id"
        const val PROJECT2_NAME = "Project2"
        const val BUILD_TYPE1_PROJECT2_ID = "project2_$BUILD_TYPE_RELEASE_NAME"
        const val BUILD_TYPE2_PROJECT2_ID = "project2_$BUILD_TYPE_NIGHTLY_NAME"

        //project3 (sub project of project1)
        const val PROJECT3_ID = "project3_id"
        const val PROJECT3_NAME = "Project3"
        const val BUILD_TYPE1_PROJECT3_ID = "project3_$BUILD_TYPE_RELEASE_NAME"
        const val BUILD_TYPE2_PROJECT3_ID = "project3_$BUILD_TYPE_NIGHTLY_NAME"
    }

    val projects = listOf(
            ProjectEntity(ProjectEntityKey(SERVER1_NAME, PROJECT1_ID), PROJECT1_NAME),
            ProjectEntity(ProjectEntityKey(SERVER1_NAME, PROJECT2_ID), PROJECT2_NAME),
            ProjectEntity(ProjectEntityKey(SERVER1_NAME, PROJECT3_ID, PROJECT1_ID), PROJECT3_NAME)
    )

    val buildTypes = listOf(
            BuildTypeEntity(BuildTypeEntityKey(SERVER1_NAME, PROJECT1_ID, BUILD_TYPE1_PROJECT1_ID), BUILD_TYPE_RELEASE_NAME),
            BuildTypeEntity(BuildTypeEntityKey(SERVER1_NAME, PROJECT1_ID, BUILD_TYPE2_PROJECT1_ID), BUILD_TYPE_NIGHTLY_NAME),
            BuildTypeEntity(BuildTypeEntityKey(SERVER1_NAME, PROJECT2_ID, BUILD_TYPE1_PROJECT2_ID), BUILD_TYPE_RELEASE_NAME),
            BuildTypeEntity(BuildTypeEntityKey(SERVER1_NAME, PROJECT2_ID, BUILD_TYPE2_PROJECT2_ID), BUILD_TYPE_NIGHTLY_NAME),
            BuildTypeEntity(BuildTypeEntityKey(SERVER1_NAME, PROJECT3_ID, BUILD_TYPE1_PROJECT3_ID), BUILD_TYPE_RELEASE_NAME),
            BuildTypeEntity(BuildTypeEntityKey(SERVER1_NAME, PROJECT3_ID, BUILD_TYPE2_PROJECT3_ID), BUILD_TYPE_NIGHTLY_NAME)
    )

    @Before
    fun prepareMocks() {
        `when`(reportTableLoader.projectRepository.getProjects(SERVER1_NAME, 0, 0)).thenReturn(projects)
        `when`(reportTableLoader.buildTypeRepository.getBuildTypesByProjectIdsAndServerNames(listOf(PROJECT1_ID, PROJECT2_ID, PROJECT3_ID), SERVER1_NAME, 0, 0)).thenReturn(buildTypes)
        `when`(reportTableLoader.buildRepository.sumBuildDurations(BUILD_TYPE1_PROJECT1_ID, PROJECT1_ID, SERVER1_NAME, 0, 0)).thenReturn(5_000)
        `when`(reportTableLoader.buildRepository.sumBuildDurations(BUILD_TYPE2_PROJECT1_ID, PROJECT1_ID, SERVER1_NAME, 0, 0)).thenReturn(3_000)
        `when`(reportTableLoader.buildRepository.sumBuildDurations(BUILD_TYPE1_PROJECT2_ID, PROJECT2_ID, SERVER1_NAME, 0, 0)).thenReturn(6_000)
        `when`(reportTableLoader.buildRepository.sumBuildDurations(BUILD_TYPE2_PROJECT2_ID, PROJECT2_ID, SERVER1_NAME, 0, 0)).thenReturn(7_000)
        `when`(reportTableLoader.buildRepository.sumBuildDurations(BUILD_TYPE1_PROJECT3_ID, PROJECT3_ID, SERVER1_NAME, 0, 0)).thenReturn(10_000)
        `when`(reportTableLoader.buildRepository.sumBuildDurations(BUILD_TYPE2_PROJECT3_ID, PROJECT3_ID, SERVER1_NAME, 0, 0)).thenReturn(1_000)
    }

    @Test
    fun loadReportModelCalculations() {
        val loadReportModel = reportTableLoader.loadReportModel(SERVER1_NAME, 0, 0, 0, 0)
        assertNotNull(loadReportModel)
        val rootNode = loadReportModel[0]
        assertEquals(32_000, rootNode.duration)
        assertPercentage(rootNode)
        loadReportModel.forEach { node ->
            if (node.id == PROJECT1_ID) {
                assertEquals(8_000, node.duration)
            }
            if (node.id == PROJECT2_ID) assertEquals(13_000, node.duration)
            if (node.id == PROJECT3_ID) assertEquals(11_000, node.duration)

            if (node.id == BUILD_TYPE1_PROJECT1_ID) assertEquals(5_000, node.duration)
            if (node.id == BUILD_TYPE2_PROJECT1_ID) assertEquals(3_000, node.duration)
            if (node.id == BUILD_TYPE1_PROJECT2_ID) assertEquals(6_000, node.duration)
            if (node.id == BUILD_TYPE2_PROJECT2_ID) assertEquals(7_000, node.duration)
            if (node.id == BUILD_TYPE1_PROJECT3_ID) assertEquals(10_000, node.duration)
            if (node.id == BUILD_TYPE2_PROJECT3_ID) assertEquals(1_000, node.duration)
        }
    }

    private fun assertPercentage(root: ReportTableNode) {
        if (root.id == "_RootTotal") {
            assertEquals(100, root.durationPercentage)
        }
        var sum = 0L
        root.childrens.forEach { child ->
            sum += child.durationPercentage
        }
        if (root.childrens.isNotEmpty()) {
            assertEquals(100, sum)
        }
        root.childrens.forEach { child ->
            assertPercentage(child)
        }
    }

}