package com.teamcity.report

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration


/**
 * @author Dmitry Zhuravlev
 *         Date:  19.10.2017
 */
@SpringBootApplication
@EnableAutoConfiguration(exclude = arrayOf(
        DataSourceAutoConfiguration::class,
        DataSourceTransactionManagerAutoConfiguration::class,
        HibernateJpaAutoConfiguration::class))
class ReportApplication

fun main(args: Array<String>) {
    SpringApplication.run(ReportApplication::class.java, *args)
}

