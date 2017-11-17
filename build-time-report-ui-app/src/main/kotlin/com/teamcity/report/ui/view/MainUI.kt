package com.teamcity.report.ui.view

import com.teamcity.report.ui.util.createNavigationButton
import com.vaadin.annotations.Theme
import com.vaadin.navigator.View
import com.vaadin.navigator.ViewDisplay
import com.vaadin.server.VaadinRequest
import com.vaadin.spring.annotation.SpringUI
import com.vaadin.spring.annotation.SpringViewDisplay
import com.vaadin.ui.*
import com.vaadin.ui.themes.ValoTheme


/**
 * @author Dmitry Zhuravlev
 *         Date:  25.10.2017
 */
@Theme("valo")
@SpringUI
@SpringViewDisplay
class MainUI : UI(), ViewDisplay {

    private lateinit var viewDisplay: Panel

    override fun init(request: VaadinRequest) {
        val root = VerticalLayout().apply {
            setSizeFull()
        }
        content = root

        val navigationBar = CssLayout().apply {
            addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP)
            addComponent(createNavigationButton("Report", ReportView.VIEW_NAME))
            addComponent(createNavigationButton("Chart", ChartView.VIEW_NAME))
            addComponent(createNavigationButton("About", AboutView.VIEW_NAME))
        }
        root.addComponent(navigationBar)

        viewDisplay = Panel().apply {
            setSizeFull()
        }
        root.addComponent(viewDisplay)
        root.setExpandRatio(viewDisplay, 1.0f)
        navigator.addView(AboutView.VIEW_NAME, AboutView::class.java)
        navigator.addView(ReportView.VIEW_NAME, ReportView::class.java)
        navigator.addView(ChartView.VIEW_NAME, ChartView::class.java)
        navigator.navigateTo(ReportView.VIEW_NAME)
    }


    override fun showView(view: View?) {
        viewDisplay.content = view as Component
    }
}