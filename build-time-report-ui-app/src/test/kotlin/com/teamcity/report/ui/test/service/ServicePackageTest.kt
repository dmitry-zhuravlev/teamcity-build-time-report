package com.teamcity.report.ui.test.service

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import

/**
 * @author Dmitry Zhuravlev
 *         Date:  01.11.2017
 */
@Import(ServicePackageConfiguration::class)
@ComponentScan(
        "com.teamcity.report.ui.service",
        "com.teamcity.report.ui.test.service")
annotation class ServicePackageTest