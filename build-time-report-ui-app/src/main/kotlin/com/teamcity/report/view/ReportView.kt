package com.teamcity.report.view

import com.teamcity.report.model.ProjectOrBuild
import com.vaadin.navigator.View
import com.vaadin.spring.annotation.SpringView
import com.vaadin.ui.*
import com.vaadin.ui.themes.ValoTheme
import java.time.LocalDateTime
import javax.annotation.PostConstruct

/**
 * @author Dmitry Zhuravlev
 *         Date:  25.10.2017
 */
@SpringView(name = ReportView.VIEW_NAME)
class ReportView : VerticalLayout(), View {
    companion object {
        const val VIEW_NAME = "report"
        const val DATE_FORMAT = "yyyy/MM/dd HH:mmZ"
    }

    @PostConstruct
    fun init() {
        setSizeFull()
        addComponent(CssLayout(
                Label("Time report interval"), Label(":"), fromDateTimeField(), Label("-"), toDateTimeField(),
                refreshButton()
        ).apply {
            addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP)
        })
        val treeGrid = treeGrid()
        addComponent(treeGrid)
        setExpandRatio(treeGrid, 1.0f)
    }

    private fun treeGrid() = TreeGrid<ProjectOrBuild>().apply {
        setSizeFull()

        addColumn(ProjectOrBuild::name).setCaption("Project/Configuration Name")
        addColumn(ProjectOrBuild::calculateDuration).setCaption("Duration (Sec)")
        setItems(testReportData(), ProjectOrBuild::childrens)
    }

    private fun fromDateTimeField() = DateTimeField().apply {
        dateFormat = DATE_FORMAT
        value = LocalDateTime.now().minusDays(5)
    }

    private fun toDateTimeField() = DateTimeField().apply {
        dateFormat = DATE_FORMAT
        value = LocalDateTime.now()
    }

    private fun refreshButton() = Button("Refresh")
}


fun testReportData() = listOf(
        ProjectOrBuild("Test Project1", childrens = listOf(
                ProjectOrBuild("Test Sub project1", 1L),
                ProjectOrBuild("Test Sub project2", 2L, childrens = listOf(
                        ProjectOrBuild("Test Sub Sub project1", 2),
                        ProjectOrBuild("Test Sub Sub project2", 2)
                )),
                ProjectOrBuild("Test Sub project3", 3L),
                ProjectOrBuild("Test Sub project4", 4L))),
        ProjectOrBuild("Test Project2", 5L)
)