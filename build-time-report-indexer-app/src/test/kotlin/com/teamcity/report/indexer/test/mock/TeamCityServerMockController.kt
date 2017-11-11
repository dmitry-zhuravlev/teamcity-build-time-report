package com.teamcity.report.indexer.test.mock

import com.google.common.io.Files
import com.teamcity.report.indexer.test.client.TestConstants.TEST_ACCESS_COOKIE
import com.teamcity.report.indexer.test.constants.TestConstants
import com.teamcity.report.indexer.test.constants.TestConstants.EMPTY_BUILDS_RESPONSE_FILE_NAME
import com.teamcity.report.indexer.test.constants.TestConstants.EMPTY_BUILD_TYPES_RESPONSE_FILE_NAME
import com.teamcity.report.indexer.test.constants.TestConstants.EMPTY_PROJECTS_RESPONSE_FILE_NAME
import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.nio.charset.StandardCharsets
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * @author Dmitry Zhuravlev
 *         Date:  30.10.2017
 */
@RestController
class TeamCityServerMockController {

    var buildsResponseFileName = TestConstants.BUILDS_RESPONSE_FILE_NAME
    var buildTypesResponseFileName = TestConstants.BUILD_TYPES_RESPONSE_FILE_NAME
    var projectsResponseFileName = TestConstants.PROJECTS_RESPONSE_FILE_NAME

    @RequestMapping(value = "/app/rest/server", method = arrayOf(RequestMethod.GET))
    fun serverInfo(request: HttpServletRequest, response: HttpServletResponse): ResponseEntity<String> {
        val body = "MockServer"
        if (isAuthRequest(request)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body)
        response.addHeader(HttpHeaders.SET_COOKIE, TEST_ACCESS_COOKIE)
        return ResponseEntity.ok(body)
    }

    @RequestMapping(value = "/httpAuth/app/rest/latest/builds", method = arrayOf(RequestMethod.GET), produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun builds(@RequestParam("fields") name: String,
               @RequestParam("locator") locator: String,
               request: HttpServletRequest, response: HttpServletResponse): ResponseEntity<String> {
        if (isAuthRequest(request)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build<String>()
        response.addHeader(HttpHeaders.SET_COOKIE, TEST_ACCESS_COOKIE)
        val buildsJSON = Files.toString(ClassPathResource(buildsResponseFileName).file, StandardCharsets.UTF_8)
        val emptyBuildsJSON = Files.toString(ClassPathResource(EMPTY_BUILDS_RESPONSE_FILE_NAME).file, StandardCharsets.UTF_8)
        val parsedLocator = parseLocator(locator)
        val start = parsedLocator["start"]
        if (start == null || start >= 13) {
            return ResponseEntity.ok(emptyBuildsJSON)
        }
        return ResponseEntity.ok(buildsJSON)
    }

    @RequestMapping(value = "/httpAuth/app/rest/latest/buildTypes", method = arrayOf(RequestMethod.GET), produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun buildTypes(@RequestParam("fields") name: String,
                   @RequestParam("locator") locator: String,
                   request: HttpServletRequest, response: HttpServletResponse): ResponseEntity<String> {
        if (isAuthRequest(request)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build<String>()
        response.addHeader(HttpHeaders.SET_COOKIE, TEST_ACCESS_COOKIE)
        val buildTypesJSON = Files.toString(ClassPathResource(buildTypesResponseFileName).file, StandardCharsets.UTF_8)
        val emptyBuildTypesJSON = Files.toString(ClassPathResource(EMPTY_BUILD_TYPES_RESPONSE_FILE_NAME).file, StandardCharsets.UTF_8)
        val parsedLocator = parseLocator(locator)
        val start = parsedLocator["start"]
        if (start == null || start >= 3) {
            return ResponseEntity.ok(emptyBuildTypesJSON)
        }
        return ResponseEntity.ok(buildTypesJSON)
    }

    @RequestMapping(value = "/httpAuth/app/rest/latest/projects", method = arrayOf(RequestMethod.GET), produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun projects(@RequestParam("fields") name: String,
                 @RequestParam("locator") locator: String,
                 request: HttpServletRequest, response: HttpServletResponse): ResponseEntity<String> {
        if (isAuthRequest(request)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build<String>()
        response.addHeader(HttpHeaders.SET_COOKIE, TEST_ACCESS_COOKIE)
        val projectsJSON = Files.toString(ClassPathResource(projectsResponseFileName).file, StandardCharsets.UTF_8)
        val emptyProjectsJSON = Files.toString(ClassPathResource(EMPTY_PROJECTS_RESPONSE_FILE_NAME).file, StandardCharsets.UTF_8)
        val parsedLocator = parseLocator(locator)
        val start = parsedLocator["start"]
        if (start == null || start >= 4) {
            return ResponseEntity.ok(emptyProjectsJSON)
        }
        return ResponseEntity.ok(projectsJSON)
    }

    private fun parseLocator(locator: String): Map<String, Int?> {
        val result = hashMapOf<String, Int?>()
        locator.split(",").forEach { locatorParam ->
            val split = locatorParam.split(":")
            result[split[0]] = split[1].toIntOrNull()
        }
        return result
    }

    private fun isAuthRequest(request: HttpServletRequest): Boolean {
        val authHeader = request.getHeader(HttpHeaders.AUTHORIZATION)
        val cookieAuthHeader = request.getHeader(HttpHeaders.COOKIE)
        if (authHeader == null && cookieAuthHeader == null) {
            return true
        }
        return false
    }

}