package com.teamcity.report.ui.view

import com.byteowls.vaadin.chartjs.ChartJs
import com.byteowls.vaadin.chartjs.config.PieChartConfig
import com.byteowls.vaadin.chartjs.data.PieDataset
import com.byteowls.vaadin.chartjs.options.Position
import com.teamcity.report.ui.service.ReportModelLoader
import com.teamcity.report.ui.service.ServerNamesLoader
import com.teamcity.report.ui.util.isValid
import com.vaadin.event.ShortcutAction
import com.vaadin.navigator.View
import com.vaadin.spring.annotation.SpringView
import com.vaadin.spring.annotation.UIScope
import com.vaadin.ui.*
import com.vaadin.ui.themes.ValoTheme
import org.springframework.beans.factory.annotation.Autowired
import java.awt.Color
import java.time.LocalDateTime
import java.time.ZoneId
import javax.annotation.PostConstruct
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.staticProperties
import kotlin.reflect.jvm.jvmErasure


/**
 * @author Dmitry Zhuravlev
 *         Date:  17.11.2017
 */
@UIScope
@SpringView(name = ChartView.VIEW_NAME)
class ChartView : VerticalLayout(), View {
    companion object {
        const val VIEW_NAME = "chart"

        val colors = Color::class.staticProperties
                .filter { it.returnType.jvmErasure.isSubclassOf(Color::class) }
                .map { it.get() as Color }
                .map { "rgba(${it.red},${it.green},${it.blue},0.5)" }
                .toSet()
                .toTypedArray()
    }

    @Autowired
    lateinit var reportModelLoader: ReportModelLoader

    @Autowired
    lateinit var serverNamesLoader: ServerNamesLoader

    lateinit var chart: ChartJs
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
        addComponent(createChart())
        setExpandRatio(chart, 1.0f)
    }

    private fun updateDateTimeFieldsRanges(from: LocalDateTime, to: LocalDateTime) {
        fromDateTimeField.rangeEnd = to
        toDateTimeField.rangeStart = from
    }

    private fun fromDateTimeField() = DateTimeField().apply {
        caption = ReportView.FROM_DATE_TIME_FIELD_CAPTION
        dateFormat = ReportView.DATE_FORMAT
        value = LocalDateTime.now().minusDays(5)
        addValueChangeListener {
            updateDateTimeFieldsRanges(value, toDateTimeField.value)
            if (isValid()) {
                refreshChart()
            }
        }
        isTextFieldEnabled = false
        fromDateTimeField = this
    }

    private fun toDateTimeField() = DateTimeField().apply {
        caption = ReportView.TO_DATE_TIME_FIELD_CAPTION
        dateFormat = ReportView.DATE_FORMAT
        value = LocalDateTime.now()
        rangeStart = fromDateTimeField.value
        addValueChangeListener {
            updateDateTimeFieldsRanges(fromDateTimeField.value, value)
            if (isValid()) {
                refreshChart()
            }
        }
        isTextFieldEnabled = false
        toDateTimeField = this
    }

    private fun serverNamesComboBox() = ComboBox<String>().apply {
        caption = ReportView.SERVER_NAMES_COMBOBOX_CAPTION
        isEmptySelectionAllowed = false
        isTextInputAllowed = true
        val list = serverNamesLoader.loadServerNames().map { serverEntity -> serverEntity.serverName }
        setItems(list)
        if (list.isNotEmpty()) setSelectedItem(list[0])
        addSelectionListener {
            refreshChart()
        }
        isSpacing = true
        serverNamesComboBox = this
    }

    private fun refreshButton() = Button(ReportView.REFRESH_BUTTON_CAPTION).apply {
        setClickShortcut(ShortcutAction.KeyCode.ENTER)
        addStyleName(ValoTheme.BUTTON_PRIMARY)
        setSizeFull()
        addClickListener {
            if (fromDateTimeField.isValid() && toDateTimeField.isValid()) {
                refreshChart()
            }
        }
    }

    fun refreshChart() {
        chart.configure(buildChartConfig())
        chart.refreshData()
    }

    private fun createChart(): ChartJs {
        chart = ChartJs(buildChartConfig())
        chart.isJsLoggingEnabled = true
        chart.setWidth("100%")
        chart.setHeight("40%")
        return chart
    }

    private fun buildChartConfig(): PieChartConfig? {
        val serverName = serverNamesComboBox.value
        val afterFinishDate = fromDateTimeField.value.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val beforeFinishDate = toDateTimeField.value.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val pieChartConfig = PieChartConfig()
        val chartData = pieChartConfig.data()
        if (serverName == null) {
            return pieChartConfig
        }
        val nodes = reportModelLoader.loadReportModelFlat(serverName, beforeFinishDate, afterFinishDate)
        chartData.labelsAsList(nodes.map { it.name })

        val serverDataset = PieDataset().label(serverName).backgroundColor(*colors)
                .borderColor("white").borderWidth(2)
                .apply {
                    for (node in nodes) {
                        addData(node.durationPercentage.toDouble())
                    }
                }
        chartData.addDataset(serverDataset)
        chartData.and().options()
                .responsive(true)
                .title()
                .display(true)
                .position(Position.TOP)
                .text("TeamCity Projects/Project configurations build time (%)")
                .and()
                .done()
        return pieChartConfig
    }


}