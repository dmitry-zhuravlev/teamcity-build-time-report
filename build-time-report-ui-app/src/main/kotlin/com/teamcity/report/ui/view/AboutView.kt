package com.teamcity.report.ui.view

import com.vaadin.navigator.View
import com.vaadin.spring.annotation.SpringView
import com.vaadin.spring.annotation.UIScope
import com.vaadin.ui.Label
import com.vaadin.ui.VerticalLayout
import javax.annotation.PostConstruct

/**
 * @author Dmitry Zhuravlev
 *         Date:  25.10.2017
 */
@UIScope
@SpringView(name = AboutView.VIEW_NAME)
class AboutView : VerticalLayout(), View {
    companion object {
        const val VIEW_NAME = "about"
    }

    @PostConstruct
    fun init() {
        addComponent(Label("TeamCity Report Application"))
    }

}