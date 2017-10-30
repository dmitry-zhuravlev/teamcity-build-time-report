package com.teamcity.report.ui.model

import com.vaadin.data.provider.AbstractHierarchicalDataProvider
import com.vaadin.data.provider.HierarchicalQuery
import com.vaadin.server.SerializablePredicate
import java.util.stream.Stream

/**
 * @author Dmitry Zhuravlev
 *         Date: 27/10/2017
 */
class ReportTableModelDataProvider : AbstractHierarchicalDataProvider<ReportTableNode, SerializablePredicate<ReportTableNode>>() {

    override fun hasChildren(node: ReportTableNode?) = node?.childrens?.isNotEmpty() ?: false

    override fun isInMemory() = false

    override fun fetchChildren(query: HierarchicalQuery<ReportTableNode, SerializablePredicate<ReportTableNode>>?): Stream<ReportTableNode> {
//        query?.parent?.
        TODO()
    }

    override fun getChildCount(query: HierarchicalQuery<ReportTableNode, SerializablePredicate<ReportTableNode>>?): Int {
        TODO()
    }
}