package com.teamcity.report.ui

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories

/**
 * @author Dmitry Zhuravlev
 *         Date:  25.10.2017
 */
@ComponentScan("com.teamcity.report")
@EnableCassandraRepositories("com.teamcity.report.repository")
@SpringBootApplication
class ReportUIApplication

fun main(args: Array<String>) {
    SpringApplication.run(ReportUIApplication::class.java, *args)
}