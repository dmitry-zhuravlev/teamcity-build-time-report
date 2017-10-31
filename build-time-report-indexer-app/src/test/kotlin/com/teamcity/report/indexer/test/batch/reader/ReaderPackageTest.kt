package com.teamcity.report.indexer.test.batch.reader

import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration
import org.springframework.boot.autoconfigure.data.cassandra.CassandraDataAutoConfiguration
import org.springframework.boot.autoconfigure.data.web.SpringDataWebAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan

/**
 * @author Dmitry Zhuravlev
 *         Date:  31.10.2017
 */
@SpringBootConfiguration
@EnableAutoConfiguration(exclude = arrayOf(
        DataSourceAutoConfiguration::class,
        DataSourceTransactionManagerAutoConfiguration::class,
        CassandraAutoConfiguration::class,
        CassandraDataAutoConfiguration::class,
        SpringDataWebAutoConfiguration::class,
        HibernateJpaAutoConfiguration::class))
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ComponentScan("com.teamcity.report.indexer.batch.reader",
        "com.teamcity.report.indexer.test.batch.reader",
        "com.teamcity.report.indexer.client",
        "com.teamcity.report.indexer.test.mock",
        "com.teamcity.report.indexer.test.config")
annotation class ReaderPackageTest