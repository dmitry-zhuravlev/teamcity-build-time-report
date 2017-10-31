package com.teamcity.report.indexer.test.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * @author Dmitry Zhuravlev
 *         Date:  31.10.2017
 */
@Component
@ConfigurationProperties("mock-server")
data class TestMockServerProperties(var id: String = "", var name: String = "", var apiVersion: String = "",
                                    var url: String = "", var username: String = "", var password: String = "")