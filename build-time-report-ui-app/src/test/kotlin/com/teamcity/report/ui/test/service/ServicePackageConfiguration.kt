package com.teamcity.report.ui.test.service

import com.teamcity.report.repository.*
import com.teamcity.report.ui.service.ReportTableModelLoader
import org.mockito.Mockito.mock
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * @author Dmitry Zhuravlev
 *         Date:  01.11.2017
 */
@Configuration
class ServicePackageConfiguration {

    @Bean
    fun reportTableModelLoader() = ReportTableModelLoader()

    @Bean
    fun buildRepository(): BuildRepository = mock(BuildRepository::class.java)

    @Bean
    fun pageableBuildTypeRepository(): PageableBuildTypeRepository = mock(PageableBuildTypeRepository::class.java)

    @Bean
    fun buildTypeRepository(): BuildTypeRepository = mock(BuildTypeRepository::class.java)

    @Bean
    fun pageableProjectRepository(): PageableProjectRepository = mock(PageableProjectRepository::class.java)

    @Bean
    fun projectRepository(): ProjectRepository = mock(ProjectRepository::class.java)
}