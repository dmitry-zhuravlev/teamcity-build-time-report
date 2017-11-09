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
            teamCityConfigProperties.servers[0].id))
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
            teamCityConfigProperties.servers[0].id))

}

@SpringBootConfiguration
class Config