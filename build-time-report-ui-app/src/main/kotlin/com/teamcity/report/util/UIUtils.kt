package com.teamcity.report.util

import com.vaadin.ui.Button
import com.vaadin.ui.UI
import com.vaadin.ui.themes.ValoTheme

/**
 * @author Dmitry Zhuravlev
 *         Date:  25.10.2017
 */
fun UI.createNavigationButton(caption: String, viewName: String): Button {
    val button = Button(caption)
    button.addStyleName(ValoTheme.BUTTON_SMALL)
    button.addClickListener({ event -> ui.navigator.navigateTo(viewName) })
    return button
}