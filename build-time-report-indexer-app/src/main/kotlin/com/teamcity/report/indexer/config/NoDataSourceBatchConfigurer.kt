package com.teamcity.report.indexer.config

import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer
import org.springframework.stereotype.Component
import javax.sql.DataSource

/**
 * @author Dmitry Zhuravlev
 *         Date:  31.10.2017
 */
@Component
class NoDataSourceBatchConfigurer : DefaultBatchConfigurer() {
    override fun setDataSource(dataSource: DataSource?) {}
}