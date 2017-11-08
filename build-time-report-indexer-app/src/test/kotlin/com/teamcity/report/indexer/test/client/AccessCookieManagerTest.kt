package com.teamcity.report.indexer.test.client

import com.teamcity.report.indexer.client.AccessCookieManager
import com.teamcity.report.indexer.properties.TeamCityConfigProperties
import com.teamcity.report.indexer.test.client.TestConstants.TEST_ACCESS_COOKIE
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringBootConfiguration
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

/**
 * @author Dmitry Zhuravlev
 * Date:  30.10.2017
 */
@RunWith(SpringRunner::class)
@ClientPackageTest
class AccessCookieManagerTest {

    @Autowired
    lateinit var teamCityConfigProperties: TeamCityConfigProperties

    @Autowired
    lateinit var accessCookieManager: AccessCookieManager

    @Test
    fun resolveAccessCookie() = assertEquals(TEST_ACCESS_COOKIE, accessCookieManager.resolveAccessCookie(
            TeamCityConfigProperties.ServerConfig(
                    id = teamCityConfigProperties.servers[0].id,
                    name = teamCityConfigProperties.servers[0].name,
                    apiVersion = teamCityConfigProperties.servers[0].apiVersion,
                    url = teamCityConfigProperties.servers[0].url,
                    username = teamCityConfigProperties.servers[0].username,
                    password = teamCityConfigProperties.servers[0].password
            )))
}

@RunWith(SpringRunner::class)
@ClientPackageTest
@ActiveProfiles("empty_credentials")
class AccessCookieManagerEmptyCredentialsTest {

    @Autowired
    lateinit var teamCityConfigProperties: TeamCityConfigProperties

    @Autowired
    lateinit var accessCookieManager: AccessCookieManager

    @Test
    fun resolveAccessCookieWithEmptyCredentials() = Assert.assertNull(accessCookieManager.resolveAccessCookie(
            TeamCityConfigProperties.ServerConfig(
                    id = teamCityConfigProperties.servers[0].id,
                    name = teamCityConfigProperties.servers[0].name,
                    apiVersion = teamCityConfigProperties.servers[0].apiVersion,
                    url = teamCityConfigProperties.servers[0].url,
                    username = teamCityConfigProperties.servers[0].username,
                    password = teamCityConfigProperties.servers[0].password
            )))

}

@SpringBootConfiguration
class Config