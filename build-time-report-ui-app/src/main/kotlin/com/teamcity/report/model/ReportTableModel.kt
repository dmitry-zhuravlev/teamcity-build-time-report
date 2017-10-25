package com.teamcity.report.model


/**
 * @author Dmitry Zhuravlev
 *         Date:  25.10.2017
 */
data class ProjectOrBuild(val name: String, val duration: Long = 0, val childrens: List<ProjectOrBuild> = emptyList()) {
    fun calculateDuration(): Long =
            if (childrens.isEmpty()) duration
            else childrens.map { child -> child.calculateDuration() }.reduce(java.lang.Long::sum)
}