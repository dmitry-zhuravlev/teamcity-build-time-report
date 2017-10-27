package com.teamcity.report.view

import com.teamcity.report.model.ReportTableNode
import com.teamcity.report.service.ReportTableModelLoader
import com.vaadin.navigator.View
import com.vaadin.spring.annotation.SpringView
import com.vaadin.ui.*
import com.vaadin.ui.themes.ValoTheme
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime
import java.time.ZoneId
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

    @Autowired
    lateinit var reportTableModelLoader: ReportTableModelLoader

    lateinit var treeGrid: TreeGrid<ReportTableNode>
    lateinit var fromDateTimeField: DateTimeField
    lateinit var toDateTimeField: DateTimeField
    lateinit var serverNameField: TextField

    @PostConstruct
    fun init() {
        setSizeFull()
        addComponent(CssLayout(
                Label("Time report interval"), Label(":"), fromDateTimeField(), Label("-"), toDateTimeField(), Label("ServerName:"), serverNameField(),
                refreshButton()
        ).apply {
            addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP)
        })
        addComponent(treeGrid())
        setExpandRatio(treeGrid, 1.0f)
    }

    private fun serverNameField() = TextField().apply {
        value = "Local TeamCity" //TODO load from server
        serverNameField = this
    }

    private fun treeGrid() = TreeGrid<ReportTableNode>().apply {
        setSizeFull()
        addColumn(ReportTableNode::name).setCaption("Project/Configuration Name")
        addColumn(ReportTableNode::calculateDuration).setCaption("Duration (Sec)")
        treeGrid = this
    }

    private fun fromDateTimeField() = DateTimeField().apply {
        dateFormat = DATE_FORMAT
        value = LocalDateTime.now().minusDays(5)
        fromDateTimeField = this
    }

    private fun toDateTimeField() = DateTimeField().apply {
        dateFormat = DATE_FORMAT
        value = LocalDateTime.now()
        toDateTimeField = this
    }

    private fun refreshButton() = Button("Refresh").apply {
        addClickListener {
            val beforeFinishDate = toDateTimeField.value.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            val afterFinishDate = fromDateTimeField.value.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            val serverName = serverNameField.value
            treeGrid.setItems(/*testReportData()*/reportTableModelLoader.loadReportModel(serverName, beforeFinishDate, afterFinishDate, 0, 100), ReportTableNode::childrens) //TODO remove hardcoded params
        }
    }
}


/*fun testReportData() = listOf(
        ProjectOrBuild("Test Project1", childrens = listOf(
                ProjectOrBuild("Test Sub project1", 1L),
                ProjectOrBuild("Test Sub project2", 2L, childrens = listOf(
                        ProjectOrBuild("Test Sub Sub project1", 2),
                        ProjectOrBuild("Test Sub Sub project2", 2)
                )),
                ProjectOrBuild("Test Sub project3", 3L),
                ProjectOrBuild("Test Sub project4", 4L))),
        ProjectOrBuild("Test Project2", 5L)
) */