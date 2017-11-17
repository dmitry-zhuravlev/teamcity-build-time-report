package com.teamcity.report.ui.view

import com.byteowls.vaadin.chartjs.ChartJs
import com.byteowls.vaadin.chartjs.config.PieChartConfig
import com.byteowls.vaadin.chartjs.data.PieDataset
import com.byteowls.vaadin.chartjs.options.Position
import com.teamcity.report.ui.model.ReportTableNode
import com.teamcity.report.ui.service.ReportTableModelLoader
import com.teamcity.report.ui.service.ServerNamesLoader
import com.vaadin.navigator.View
import com.vaadin.spring.annotation.SpringView
import com.vaadin.spring.annotation.UIScope
import com.vaadin.ui.VerticalLayout
import org.springframework.beans.factory.annotation.Autowired
import java.time.ZonedDateTime
import javax.annotation.PostConstruct


/**
 * @author Dmitry Zhuravlev
 *         Date:  17.11.2017
 */
@UIScope
@SpringView(name = ChartView.VIEW_NAME)
class ChartView : VerticalLayout(), View {
    companion object {
        const val VIEW_NAME = "chart"
    }

    @Autowired
    lateinit var reportTableModelLoader: ReportTableModelLoader

    @Autowired
    lateinit var serverNamesLoader: ServerNamesLoader

    @PostConstruct
    fun init() {
        setSizeFull()
        addComponent(createChart())
    }

    private fun createChart(): ChartJs {
        val loadServerNames = serverNamesLoader.loadServerNames().map { it.serverName }.toTypedArray()
        val afterFinishDate = ZonedDateTime.now().minusYears(5).toInstant().toEpochMilli()
        val beforeFinishDate = ZonedDateTime.now().toInstant().toEpochMilli()
        val pieChartConfig = PieChartConfig()
        val chartData = pieChartConfig.data().labels(*loadServerNames)

        loadServerNames.forEach { serverName ->
            val total = reportTableModelLoader.loadReportModel(serverName, beforeFinishDate, afterFinishDate)[0]
            val tableNodes = total.childrens.collectAllTableNodes()
            chartData.labelsAsList(tableNodes.map { it.name })
            val serverDataset = PieDataset().label(serverName).backgroundColor("rgba(151,187,205,0.5)").borderColor("white").borderWidth(2)
                    .apply {
                        for (node in tableNodes) {

                            addData(node.duration.toDouble())
                        }
                    }
            chartData.addDataset(serverDataset)
        }
        chartData.and().options()
                .responsive(true)
                .title()
                .display(true)
                .position(Position.TOP)
                .text("TeamCity Projects/Project configurations build time")
                .and()
                .done()

        val chart = ChartJs(pieChartConfig)
        chart.isJsLoggingEnabled = true
        chart.setWidth("100%")
        chart.setHeight("50%")
        return chart
    }

    private fun List<ReportTableNode>.collectAllTableNodes(nodeList: MutableList<ReportTableNode> = mutableListOf()): List<ReportTableNode> {
        forEach { node ->
            nodeList.add(node)
            node.childrens.collectAllTableNodes(nodeList)
        }
        return nodeList
    }
}