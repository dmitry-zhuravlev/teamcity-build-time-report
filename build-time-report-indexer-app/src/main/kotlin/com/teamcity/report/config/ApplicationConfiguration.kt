package com.teamcity.report.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

/**
 * @author Dmitry Zhuravlev
 *         Date:  19.10.2017
 */
@Configuration
@ComponentScan("com.teamcity.report")
class ApplicationConfiguration {
    @Bean
    fun restTemplate() = RestTemplate()

//    @Bean
//    fun buildsIndexerWorkers() = listOf<BuildsIndexerWorker>()
}