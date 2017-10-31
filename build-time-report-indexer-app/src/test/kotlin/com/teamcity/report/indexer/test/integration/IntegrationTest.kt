package com.teamcity.report.indexer.test.integration

import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
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
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ComponentScan(
        "com.teamcity.report.indexer.batch",
        "com.teamcity.report.indexer.client",
        "com.teamcity.report.indexer.rest",
        "com.teamcity.report.indexer.properties",
        "com.teamcity.report.repository",
        "com.teamcity.report.indexer.test.mock",
        "com.teamcity.report.indexer.test.integration")
@EnableAutoConfiguration(exclude = arrayOf(
        DataSourceAutoConfiguration::class,
        DataSourceTransactionManagerAutoConfiguration::class,
        HibernateJpaAutoConfiguration::class))
annotation class IntegrationTest