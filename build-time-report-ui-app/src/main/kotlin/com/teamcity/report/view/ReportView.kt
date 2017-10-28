package com.teamcity.report.view

import com.teamcity.report.model.ReportTableNode
import com.teamcity.report.service.ReportTableModelLoader
import com.teamcity.report.service.ServerNamesLoader
import com.vaadin.event.ShortcutAction
import com.vaadin.navigator.View
import com.vaadin.spring.annotation.SpringView
import com.vaadin.ui.*
import com.vaadin.ui.themes.ValoTheme
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit
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

    @Autowired
    lateinit var serverNamesLoader: ServerNamesLoader

    lateinit var treeGrid: TreeGrid<ReportTableNode>
    lateinit var fromDateTimeField: DateTimeField
    lateinit var toDateTimeField: DateTimeField
    lateinit var serverNamesComboBox: ComboBox<String>

    @PostConstruct
    fun init() {
        setSizeFull()
        addComponent(HorizontalLayout(
                FormLayout(fromDateTimeField()), FormLayout(toDateTimeField()), FormLayout(serverNamesComboBox()),
                FormLayout(refreshButton())
        ))
        addComponent(treeGrid())
        setExpandRatio(treeGrid, 1.0f)
    }

    private fun fromDateTimeField() = DateTimeField().apply {
        caption = "From"
        dateFormat = DATE_FORMAT
        value = LocalDateTime.now().minusDays(5)
        fromDateTimeField = this
    }

    private fun toDateTimeField() = DateTimeField().apply {
        caption = "To"
        dateFormat = DATE_FORMAT
        value = LocalDateTime.now()
        toDateTimeField = this
    }

    private fun serverNamesComboBox() = ComboBox<String>().apply {
        caption = "Server"
        isEmptySelectionAllowed = false
        isTextInputAllowed = true
        val list = serverNamesLoader.loadServerNames().map { serverEntity -> serverEntity.serverName }
        setItems(list)
        if (list.isNotEmpty()) setSelectedItem(list[0])
        isSpacing = true
        serverNamesComboBox = this
    }

    private fun refreshButton() = Button("Refresh").apply {
        setClickShortcut(ShortcutAction.KeyCode.ENTER)
        addStyleName(ValoTheme.BUTTON_PRIMARY)
        setSizeFull()
        addClickListener {
            refreshTreeGridItems()
        }
    }

    private fun treeGrid() = TreeGrid<ReportTableNode>().apply {
        setSizeFull()
        addColumn(ReportTableNode::name).caption = "Project/Configuration Name"
        addColumn { reportTableNode -> durationRepresentation(reportTableNode.calculateDuration()) }.caption = "Duration"
        treeGrid = this
    }

    fun durationRepresentation(duration: Long): String {
        val second = TimeUnit.MILLISECONDS.toSeconds(duration)
        val minute = TimeUnit.MILLISECONDS.toMinutes(duration)
        return if (minute > 0) minute.toString() + " min " else "" +
                if (second > 0) second.toString() + " sec" else "" +
                        if (minute < 0 && second < 0) second.toString() + " millis" else "0"
    }

    private fun refreshTreeGridItems() {
        val beforeFinishDate = toDateTimeField.value.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val afterFinishDate = fromDateTimeField.value.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val serverName = serverNamesComboBox.value
        val reportItems = reportTableModelLoader.loadReportModel(serverName, beforeFinishDate, afterFinishDate, 0, 100)//TODO remove hardcoded params
        treeGrid.setItems(reportItems, ReportTableNode::childrens)
        treeGrid.expand(reportItems)
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