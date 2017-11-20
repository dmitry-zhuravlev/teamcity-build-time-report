package com.teamcity.report.ui.view

import com.teamcity.report.ui.model.ReportNode
import com.teamcity.report.ui.service.ReportModelLoader
import com.teamcity.report.ui.service.ServerNamesLoader
import com.teamcity.report.ui.util.durationRepresentation
import com.teamcity.report.ui.util.isValid
import com.teamcity.report.ui.util.percentageDurationRepresentation
import com.vaadin.event.ShortcutAction
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
        const val DATE_FORMAT = "yyyy/MM/dd HH:mm"//Z"

        const val PROJECT_OR_CONFIG_TABLE_CAPTION = "Project/Configuration Name"
        const val DURATION_TABLE_CAPTION = "Duration"
        const val PERCENTAGE_DURATION_TABLE_CAPTION = "%"

        const val FROM_DATE_TIME_FIELD_CAPTION = "From"
        const val TO_DATE_TIME_FIELD_CAPTION = "To"
        const val SERVER_NAMES_COMBOBOX_CAPTION = "Server"
        const val REFRESH_BUTTON_CAPTION = "Refresh"
    }

    @Autowired
    lateinit var reportModelLoader: ReportModelLoader

    @Autowired
    lateinit var serverNamesLoader: ServerNamesLoader

    lateinit var treeGrid: TreeGrid<ReportNode>
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
        updateDateTimeFieldsRanges(fromDateTimeField.value, toDateTimeField.value)
        addComponent(treeGrid())
        setExpandRatio(treeGrid, 1.0f)
        refreshTreeGridItems()
    }

    private fun updateDateTimeFieldsRanges(from: LocalDateTime, to: LocalDateTime) {
        fromDateTimeField.rangeEnd = to
        toDateTimeField.rangeStart = from
    }

    private fun fromDateTimeField() = DateTimeField().apply {
        caption = FROM_DATE_TIME_FIELD_CAPTION
        dateFormat = DATE_FORMAT
        value = LocalDateTime.now().minusDays(5)
        addValueChangeListener {
            updateDateTimeFieldsRanges(value, toDateTimeField.value)
            if (isValid()) {
                refreshTreeGridItems()
            }
        }
        isTextFieldEnabled = false
        fromDateTimeField = this
    }

    private fun toDateTimeField() = DateTimeField().apply {
        caption = TO_DATE_TIME_FIELD_CAPTION
        dateFormat = DATE_FORMAT
        value = LocalDateTime.now()
        rangeStart = fromDateTimeField.value
        addValueChangeListener {
            updateDateTimeFieldsRanges(fromDateTimeField.value, value)
            if (isValid()) {
                refreshTreeGridItems()
            }
        }
        isTextFieldEnabled = false
        toDateTimeField = this
    }

    private fun serverNamesComboBox() = ComboBox<String>().apply {
        caption = SERVER_NAMES_COMBOBOX_CAPTION
        isEmptySelectionAllowed = false
        isTextInputAllowed = true
        val list = serverNamesLoader.loadServerNames().map { serverEntity -> serverEntity.serverName }
        setItems(list)
        if (list.isNotEmpty()) setSelectedItem(list[0])
        addSelectionListener {
            refreshTreeGridItems()
        }
        isSpacing = true
        serverNamesComboBox = this
    }

    private fun refreshButton() = Button(REFRESH_BUTTON_CAPTION).apply {
        setClickShortcut(ShortcutAction.KeyCode.ENTER)
        addStyleName(ValoTheme.BUTTON_PRIMARY)
        setSizeFull()
        addClickListener {
            if (fromDateTimeField.isValid() && toDateTimeField.isValid()) {
                refreshTreeGridItems()
            }
        }
    }

    private fun treeGrid() = TreeGrid<ReportNode>().apply {
        setSizeFull()
        addColumn(ReportNode::name).caption = PROJECT_OR_CONFIG_TABLE_CAPTION
        addColumn { reportTableNode -> durationRepresentation(reportTableNode.duration) }.caption = DURATION_TABLE_CAPTION
        addColumn { reportTableNode -> percentageDurationRepresentation(reportTableNode.durationPercentage) }.caption = PERCENTAGE_DURATION_TABLE_CAPTION
        treeGrid = this
    }

    private fun refreshTreeGridItems() {
        val afterFinishDate = fromDateTimeField.value.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val beforeFinishDate = toDateTimeField.value.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val serverName = serverNamesComboBox.value ?: return
        val reportItems = reportModelLoader.loadReportModel(serverName, beforeFinishDate, afterFinishDate)
        treeGrid.setItems(reportItems, ReportNode::childrens)
        treeGrid.dataProvider.refreshAll()
        treeGrid.expand(reportItems)
    }
}