package com.teamcity.report.model

import com.teamcity.report.repository.entity.ROOT_PARENT_PROJECT_ID


/**
 * @author Dmitry Zhuravlev
 *         Date:  25.10.2017
 */
data class ReportTableNode(val id: String, val name: String, var duration: Long = 0, var durationPercentage: Long = 100L, val childrens: MutableList<ReportTableNode> = mutableListOf(), var parentId: String = ROOT_PARENT_PROJECT_ID) {

    fun calculateDuration(): Long =
            if (childrens.isEmpty()) duration
            else {
                duration = childrens.map { child -> child.calculateDuration() }.reduce(java.lang.Long::sum)
                duration
            }

    fun calculatePercentageDuration(totalDuration: Long): Long {
        durationPercentage = Math.round((duration * 100).toDouble() / totalDuration.toDouble())
        childrens.forEach { child ->
            child.calculatePercentageDuration(duration)
        }
        return durationPercentage
    }
}
