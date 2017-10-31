package com.teamcity.report.indexer.test.client

import com.teamcity.report.indexer.client.AccessCookieManager
import com.teamcity.report.indexer.properties.TeamCityConfigProperties
import com.teamcity.report.indexer.test.client.TestConstants.TEST_ACCESS_COOKIE
import com.teamcity.report.indexer.test.config.TestMockServerProperties
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.junit4.SpringRunner

/**
 * @author Dmitry Zhuravlev
 * Date:  30.10.2017
 */
@RunWith(SpringRunner::class)
@ClientPackageTest
class AccessCookieManagerTest {

    @Autowired
    lateinit var testMockServerProperties: TestMockServerProperties

    @Test
    fun resolveAccessCookie() = assertEquals(TEST_ACCESS_COOKIE, AccessCookieManager().resolveAccessCookie(
            TeamCityConfigProperties.ServerConfig(
                    id = testMockServerProperties.id,
                    name = testMockServerProperties.name,
                    apiVersion = testMockServerProperties.apiVersion,
                    url = testMockServerProperties.url,
                    username = testMockServerProperties.username,
                    password = testMockServerProperties.password
            )))

    @Test
    fun resolveAccessCookieWithEmptyCredentials() = assertNull(AccessCookieManager().resolveAccessCookie(
            TeamCityConfigProperties.ServerConfig(
                    id = testMockServerProperties.id,
                    name = testMockServerProperties.name,
                    apiVersion = testMockServerProperties.apiVersion,
                    url = testMockServerProperties.url,
                    username = "",
                    password = ""
            )))

}