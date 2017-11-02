package com.teamcity.report.ui.model

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

    /*fun calculateDuration(): Long {
        val nodes = Stack<Pair<Boolean, ReportTableNode>>()
        val durations = Stack<Long>()

        //init
        nodes.push(false to this)

        //main cycle
        while (!nodes.empty()) {
            //if marked then -> pop from stack, set duration, increase duration of upper level
            if (nodes.peek().first) {
                val tmp = durations.pop()
                nodes.pop().second.duration = tmp
                if (!durations.empty()) {
                    durations.push(tmp + durations.pop())
                }
            } else { //top of stack is not marked
                //top is leaf-node -> pop it from stack and increase duration of upper level
                if (nodes.peek().second.childrens.isEmpty()) {
                    val tmpDuration = nodes.pop().second.duration
                    durations.push(tmpDuration + durations.pop())
                } else { //top is not leaf-node -> mark it, add new duration level (duration stack) and put its children in stack
                    nodes.push(Pair(true, nodes.pop().second))
                    durations.push(0L)
                    nodes.peek().let { (_, node) -> node.childrens.forEach { nodes.push(false to it) } }
                }
            }
        }
        return duration
    }*/

/*    fun calculatePercentageDuration(upperLevelDuration: Long): Long {
        val nodes = Stack<Pair<Boolean, ReportTableNode>>()
        val durations = Stack<Long>()

        //init
        nodes.push(false to this)
        durationPercentage = Math.round((duration * 100).toDouble() / upperLevelDuration.toDouble())

        //main cycle
        while (!nodes.empty()) {
            //if marked then -> pop it
            if (nodes.peek().first) {
                nodes.pop()
                durations.pop()
            } else { //top of stack is not marked
                //top is leaf-node -> pop it
                if (nodes.peek().second.childrens.isEmpty()) nodes.pop()
                else { //top is not leaf-node -> mark it , add new duration level, calculate percents for children and put its children in stack
                    nodes.push(true to nodes.pop().second)
                    val tmpDuration = durations.push(nodes.peek().second.duration)
                    nodes.peek().let { (_, node) ->
                        node.childrens.forEach { child ->
                            child.durationPercentage = Math.round((child.duration * 100).toDouble() / tmpDuration.toDouble())
                            nodes.push(false to child)
                        }
                    }
                }
            }
        }
        return durationPercentage
    }*/
}
