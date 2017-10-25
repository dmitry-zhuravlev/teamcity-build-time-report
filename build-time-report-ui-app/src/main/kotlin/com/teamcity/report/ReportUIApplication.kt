package com.teamcity.report

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

/**
 * @author Dmitry Zhuravlev
 *         Date:  25.10.2017
 */
@SpringBootApplication
class ReportUIApplication

fun main(args: Array<String>) {
    SpringApplication.run(ReportUIApplication::class.java, *args)
}