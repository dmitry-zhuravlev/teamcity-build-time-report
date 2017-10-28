package com.teamcity.report.model

import com.teamcity.report.repository.entity.ROOT_PARENT_PROJECT_ID


/**
 * @author Dmitry Zhuravlev
 *         Date:  25.10.2017
 */
data class ReportTableNode(val id: String, val name: String, var duration: Long = 0, val childrens: MutableList<ReportTableNode> = mutableListOf(), var parentId: String = ROOT_PARENT_PROJECT_ID) {
    fun calculateDuration(): Long =
            if (childrens.isEmpty()) duration
            else childrens.map { child -> child.calculateDuration() }.reduce(java.lang.Long::sum)
}