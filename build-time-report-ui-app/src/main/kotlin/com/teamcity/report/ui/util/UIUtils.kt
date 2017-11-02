package com.teamcity.report.ui.util

import com.vaadin.ui.Button
import com.vaadin.ui.UI
import com.vaadin.ui.themes.ValoTheme
import java.util.concurrent.TimeUnit

/**
 * @author Dmitry Zhuravlev
 *         Date:  25.10.2017
 */
fun UI.createNavigationButton(caption: String, viewName: String): Button {
    val button = Button(caption)
    button.addStyleName(ValoTheme.BUTTON_SMALL)
    button.addClickListener { ui.navigator.navigateTo(viewName) }
    return button
}

fun percentageDurationRepresentation(duration: Long): String {
    return "$duration%"
}

fun durationRepresentation(durationMillis: Long): String {
    val hour = TimeUnit.MILLISECONDS.toHours(durationMillis)
    val minute = TimeUnit.MILLISECONDS.toMinutes(durationMillis) -
            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(durationMillis))
    val second = TimeUnit.MILLISECONDS.toSeconds(durationMillis) -
            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(durationMillis))
    return StringBuilder().apply {
        if (hour > 0) append(hour).append(" hour ")
        if (minute > 0) append(minute).append(" min ")
        if (second > 0) append(second).append(" sec ")
        if (isBlank()) append(durationMillis).append(" ms ")
    }.toString()
}